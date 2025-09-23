package jp.mincra.mincramagics.oraxen.mechanic.broom;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// 状態:
/// - ブレーキ中: ブレーキ性能に応じて flying_speed を上げる
/// - 高度 X 以上: スピードを大きく下げる
public class BroomMechanicManager implements Listener, PacketListener {

    private final BroomMechanicFactory factory;
    private final NamespacedKey broomKey;
    private final Map<UUID, BroomData> activeBrooms = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerInput> playerInputs = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastPlayerLocations = new ConcurrentHashMap<>();
    private final ContinuousSoundPlayer soundPlayer = new ContinuousSoundPlayer();

    private final NamespacedKey ATTR_MAX_HEIGHT = new NamespacedKey(MincraMagics.getInstance(), "broom_max_height");
    private final NamespacedKey ATTR_BRAKE_DISTANCE = new NamespacedKey(MincraMagics.getInstance(), "broom_brake_distance");
    private final NamespacedKey ATTR_ASCENT_POWER = new NamespacedKey(MincraMagics.getInstance(), "broom_ascent_power");

    public BroomMechanicManager(BroomMechanicFactory factory, JavaPlugin plugin) {
        this.factory = factory;
        this.broomKey = new NamespacedKey(plugin, "broom_entity");

        final var vol = 0.07f;
        final var pitch = 0.8f;
        final var lengthTicks = 250;
        final var soundCategories = List.of(
                SoundCategory.MASTER,
                SoundCategory.UI,
                SoundCategory.NEUTRAL,
                SoundCategory.PLAYERS,
                SoundCategory.BLOCKS,
                SoundCategory.HOSTILE,
                SoundCategory.AMBIENT,
                SoundCategory.WEATHER,
                SoundCategory.VOICE
        );

        new BKTween(plugin)
                .execute(() -> {
                    for (final var broom : activeBrooms.entrySet()) {
                        final var broomData = broom.getValue();

                        // 速度の大きさに応じて音を流す
                        final var vehicle = broomData.vehicle();
                        final var passenger = (Player) vehicle.getPassengers().stream().filter(e -> e instanceof Player).findFirst().orElse(null);
                        if (passenger == null) continue;

                        // VehicleMoveEvent や vehicle.getVelocity() が常に (0,0,0) を返すので、自前で速度を計算する
                        final var currentLocation = passenger.getLocation();
                        final var lastLocation = lastPlayerLocations.getOrDefault(passenger.getUniqueId(), currentLocation);
                        final var velocity = currentLocation.toVector().subtract(lastLocation.toVector());
                        lastPlayerLocations.put(passenger.getUniqueId(), currentLocation);

                        final int speed = (int) velocity.length();

                        if (speed >= 1) {
                            soundPlayer.playSound(passenger, Sound.ITEM_ELYTRA_FLYING, SoundCategory.MASTER, vol * speed, pitch, lengthTicks);
                        } else {
                            soundPlayer.stopSound(passenger, Sound.ITEM_ELYTRA_FLYING, SoundCategory.MASTER);
                        }

                        final var mechanic = broomData.mechanic();

                        // max height
                        final boolean shouldMaxHeight = vehicle.getLocation().getY() > mechanic.getMaxHeight() && velocity.getY() > 0;
                        final double maxHeightFactor = shouldMaxHeight ? 0.01 : 1.0;

                        // braking
                        // 逆向きに進んでいたら brakeDistance に応じて速度を落とす
                        final var input = playerInputs.get(passenger.getUniqueId());
                        final var passengerDir = passenger.getLocation().getDirection();
                        final var lookDir = passengerDir.setY(0).normalize();
                        final var moveDir = velocity.clone().setY(0).normalize();
                        final var dot = lookDir.dot(moveDir);
                        final boolean isBraking = (dot < -0.1 && input.forward()) || (dot > 0.1 && input.backward());
                        final double brakeFactor = isBraking ? 0.001 / Math.max(mechanic.getBrakeDistance(), 0.0000001) : 1.0;

                        // ascent power
                        final boolean isAscending = (input.forward() && passengerDir.getY() > 0) || input.jump();
                        final double ascentFactor = isAscending ? (mechanic.getAscentPower() / 10) : 0.0;

                        final var attr = vehicle.getAttribute(Attribute.FLYING_SPEED);
                        if (attr != null) {
                            final var maxHeightMod = attr.getModifier(ATTR_MAX_HEIGHT);
                            final var brakeMod = attr.getModifier(ATTR_BRAKE_DISTANCE);
                            final var ascentMod = attr.getModifier(ATTR_ASCENT_POWER);
                            if (maxHeightMod != null) attr.removeModifier(maxHeightMod);
                            if (brakeMod != null) attr.removeModifier(brakeMod);
                            if (ascentMod != null) attr.removeModifier(ascentMod);
                            attr.addModifier(new AttributeModifier(ATTR_MAX_HEIGHT, maxHeightFactor, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                            attr.addModifier(new AttributeModifier(ATTR_BRAKE_DISTANCE, brakeFactor, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                            attr.addModifier(new AttributeModifier(ATTR_ASCENT_POWER, ascentFactor, AttributeModifier.Operation.ADD_NUMBER));
                        } else {
                            vehicle.registerAttribute(Attribute.FLYING_SPEED);
                        }
                    }
                    return true;
                })
                .repeat(TickTime.TICK, 2, 0, -1)
                .run();
    }

    //  箒アイテムで地面を右クリック -> 箒をスポーンさせる
    @EventHandler
    public void onBroomPlace(PlayerInteractEvent event) {
        MincraLogger.debug("PlayerInteractEvent: " + event);
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        MincraLogger.debug("Interacted with item: " + item);

        if (factory.isNotImplementedIn(item)) return;

        final var mechanic = factory.getMechanic(item);
        MincraLogger.debug("Retrieved mechanic: " + mechanic);

        if (mechanic instanceof BroomMechanic broomMechanic) {
            MincraLogger.debug("Placing broom with item: " + item);
            event.setCancelled(true);
            final var clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;

            spawnBroom(event.getPlayer(), clickedBlock.getLocation().add(0.5, 1.2, 0.5), broomMechanic, item.clone());
            item.setAmount(item.getAmount() - 1);
        }
    }

    private void spawnBroom(Player owner, Location location, BroomMechanic mechanic, ItemStack item) {
        HappyGhast broomEntity = location.getWorld().spawn(location, HappyGhast.class, as -> {
            as.setInvisible(true);

            as.setCustomNameVisible(false);
            as.setSilent(true);
            as.setRemoveWhenFarAway(false);

            // happy ghast
            as.setAI(false);
            as.registerAttribute(Attribute.FLYING_SPEED);
            as.registerAttribute(Attribute.SCALE);
            as.registerAttribute(Attribute.MAX_HEALTH);
            final var flyingSpeedAttr = as.getAttribute(Attribute.FLYING_SPEED);
            final var scaleAttr = as.getAttribute(Attribute.SCALE);
            final var healthAttr = as.getAttribute(Attribute.MAX_HEALTH);
            if (flyingSpeedAttr != null) flyingSpeedAttr.setBaseValue(mechanic.getSpeed() / 10);
            if (scaleAttr != null) scaleAttr.setBaseValue(0.1);
            if (healthAttr != null) healthAttr.setBaseValue(6);

            final var harness = new ItemStack(Material.WHITE_HARNESS);
            final var meta = harness.getItemMeta();
            if (meta != null) {
                final var equippable = meta.getEquippable();
                equippable.setModel(new NamespacedKey("mincra", "transparent_harness"));
                equippable.setSlot(EquipmentSlot.BODY);
                meta.setEquippable(equippable);
                harness.setItemMeta(meta);
            }
            as.getEquipment().setItem(EquipmentSlot.BODY, harness);

            as.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 2, true, false, false));
            as.getPersistentDataContainer().set(broomKey, PersistentDataType.STRING, mechanic.getModelId());
        });
        MincraLogger.debug("Spawned broom entity: " + broomEntity);

        final var modeledEntity = ModelEngineAPI.createModeledEntity(broomEntity);
        final var activeModel = ModelEngineAPI.createActiveModel(mechanic.getModelId());
        modeledEntity.addModel(activeModel, true);
        MincraLogger.debug("Created modeled entity with model: " + mechanic.getModelId());
//        modeledEntity.detectPlayers();

        location.getWorld().playSound(location, Sound.BLOCK_ROOTS_PLACE, 1.0f, 0.5f);
        location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, location, 10, 0.4, 0.4, 0.4, 0.05, Material.SOUL_SAND.createBlockData());

        final var dropItem = item.clone();
        dropItem.setAmount(1);
        activeBrooms.put(broomEntity.getUniqueId(), new BroomData(mechanic, activeModel, dropItem, broomEntity));
        playerInputs.put(owner.getUniqueId(), new PlayerInput(false, false, false, false, false, false));
    }

    @EventHandler
    public void onBroomBreak(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Entity target = event.getEntity();

        if (target.getPersistentDataContainer().has(broomKey, PersistentDataType.STRING)) {
            BroomData data = activeBrooms.remove(target.getUniqueId());
            if (data != null) {
                ModelEngineAPI.removeModeledEntity(target.getUniqueId());
                // drop the broom item
                target.getWorld().dropItemNaturally(target.getLocation(), data.item());
            }
            target.remove();

            Location location = target.getLocation();

            location.getWorld().playSound(location, Sound.BLOCK_ROOTS_BREAK, 1.0f, 0.5f);
            location.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, location, 10, 0.4, 0.4, 0.4, 0.05, Material.SOUL_SAND.createBlockData());

            activeBrooms.remove(target.getUniqueId());
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        try {
            if (event.getPacketType() == PacketType.Play.Server.SOUND_EFFECT) {
                final var packet = new WrapperPlayServerSoundEffect(event.clone());
//                MincraLogger.debug("SOUND_EFFECT: " + packet.getSound().getSoundId().getKey());
                if (List.of(
                        new ResourceLocation("minecraft", "entity.happy_ghast.harness_goggles_up"),
                        new ResourceLocation("minecraft", "entity.happy_ghast.harness_goggles_down")
                ).contains(packet.getSound().getSoundId())) {
                    event.setCancelled(true);
                    MincraLogger.debug("Cancelled ghast equip/unequip sound");
                }
            }
        } catch (Exception e) {
            MincraLogger.error("Error in onPacketSend: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 4. パケットをリッスンして飛行操作
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLAYER_INPUT) {
            return;
        }
        if (!(event.getPlayer() instanceof Player player)) return;

        Entity vehicle = player.getVehicle();
        if (vehicle == null || !activeBrooms.containsKey(vehicle.getUniqueId())) {
            return;
        }

        WrapperPlayClientPlayerInput packet = new WrapperPlayClientPlayerInput(event);

        playerInputs.put(player.getUniqueId(), new PlayerInput(
                packet.isForward(),
                packet.isBackward(),
                packet.isLeft(),
                packet.isRight(),
                packet.isJump(),
                packet.isShift()
        ));
    }
}

record BroomData(BroomMechanic mechanic, ActiveModel activeModel, @NotNull ItemStack item, HappyGhast vehicle) {
}

record PlayerInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean shift) {
    @Override
    public String toString() {
        return "PlayerInput{" +
                "forward=" + forward +
                ", backward=" + backward +
                ", left=" + left +
                ", right=" + right +
                ", jump=" + jump +
                ", shift=" + shift +
                '}';
    }
}

class ContinuousSoundPlayer {
    private final Map<Player, Map<SoundCategory, Map<Sound, Integer>>> lastPlayedTicks = new ConcurrentHashMap<>();

    public void playSound(Player player, Sound sound, SoundCategory category, float volume, float pitch, int lengthTicks) {
        long currentTick = Bukkit.getCurrentTick();

        lastPlayedTicks.putIfAbsent(player, new ConcurrentHashMap<>());
        lastPlayedTicks.get(player).putIfAbsent(category, new ConcurrentHashMap<>());

        Map<Sound, Integer> soundMap = lastPlayedTicks.get(player).get(category);
        int lastTick = soundMap.getOrDefault(sound, -lengthTicks);

        if (currentTick - lastTick >= lengthTicks) {
            player.playSound(player.getLocation(), sound, category, volume, pitch);
            soundMap.put(sound, (int) currentTick);
        }
    }

    public void stopSound(Player player, Sound sound, SoundCategory category) {
        player.stopSound(sound, category);

        // Remove from last played ticks to allow future playback
        if (lastPlayedTicks.containsKey(player) && lastPlayedTicks.get(player).containsKey(category)) {
            lastPlayedTicks.get(player).get(category).remove(sound);
        }
    }
}
