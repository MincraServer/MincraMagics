package jp.mincra.mincramagics.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * エンティティの頭上にダメージインジケータを表示・管理するクラス (Passenger方式)。
 */
public final class DamageIndicator implements Listener {
    static final String METADATA_KEY = "DamageIndicator";

    private final JavaPlugin plugin;
    // 1行目 (モブの場合は CustomName, プレイヤーの場合は体力バー)
    private final Map<UUID, ArmorStand> lowerLineArmorStands = new HashMap<>();
    // 2行目 (モブの場合は元の名前、プレイヤーの場合はなし)
    private final Map<UUID, ArmorStand> upperLineArmorStands = new HashMap<>();
    private final Map<UUID, BukkitTask> cleanupTasks = new HashMap<>();

    public DamageIndicator(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void removeAll() {
        final var parentIds = this.lowerLineArmorStands.keySet().stream().toList();
        for (final var entityId : parentIds) {
            this.removeIndicator(entityId);
        }
    }

    // 他のプラグインによるダメージキャンセルを考慮し、EventPriorityをMONITORに設定
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getEntity() instanceof ArmorStand) {
            return;
        }

//        if (entity.getBossBar() != null) return;

        final boolean playerNearby = entity.getWorld().getPlayers().stream()
                .anyMatch(p -> p.getLocation().distanceSquared(entity.getLocation()) <= 400); // 20*20=400
        if (!playerNearby) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                // runnable実行時にエンティティが無効になっている可能性をチェック
                if (entity.isDead() || !entity.isValid()) {
                    removeIndicator(entity.getUniqueId());
                    return;
                }
                updateIndicator(entity);
            }
        }.runTaskLater(this.plugin, 1L);
    }

    @EventHandler
    private void onEntityDeath(final EntityDeathEvent event) {
        this.removeIndicator(event.getEntity().getUniqueId());
    }

    @EventHandler
    private void onPlayerQuit(final PlayerQuitEvent event) {
        this.removeIndicator(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerKick(final PlayerKickEvent event) {
        this.removeIndicator(event.getPlayer().getUniqueId());
    }

    /**
     * プレイヤー: 下段なし, 上段に体力バー
     * モブ: 下段に体力バー, 上段に元の名前
     * @param entity HPバーを表示するエンティティ
     */
    private void updateIndicator(final LivingEntity entity) {
        final UUID entityId = entity.getUniqueId();
        final var lowerLine = Optional.ofNullable(this.lowerLineArmorStands.get(entityId))
                .orElseGet(() -> {
                    if (entity instanceof Player) return null;
                    final var loc = entity.getLocation().add(0, entity.getHeight(), 0);
                    final var armorStand = loc.getWorld().spawn(loc, ArmorStand.class, as -> {
                        as.setGravity(false);
                        as.setVisible(false);
                        as.setInvisible(true);
                        as.setCustomNameVisible(true);
                        as.setMarker(true);
                        as.setMetadata(METADATA_KEY, new FixedMetadataValue(this.plugin, true));
                    });
                    entity.addPassenger(armorStand);
                    this.lowerLineArmorStands.put(entityId, armorStand);
                    return armorStand;
                });
        final var upperLine = Optional.ofNullable(this.upperLineArmorStands.get(entityId))
                .orElseGet(() -> {
                    final var loc = entity.getLocation().add(0, entity.getHeight(), 0);
                    final var armorStand = loc.getWorld().spawn(loc, ArmorStand.class, as -> {
                        as.setGravity(false);
                        as.setVisible(false);
                        as.setInvisible(true);
                        as.setCustomNameVisible(true);
                        as.setMetadata(METADATA_KEY, new FixedMetadataValue(this.plugin, true));
                        as.registerAttribute(Attribute.SCALE);
                        final var scaleAttr = as.getAttribute(Attribute.SCALE);
                        if (scaleAttr != null) {
                            scaleAttr.setBaseValue(0.15); // 非常に小さくする
                        }
                    });
                    entity.addPassenger(armorStand);
                    this.upperLineArmorStands.put(entityId, armorStand);
                    return armorStand;
                });
        var customNameEnt = entity instanceof Player ? null : upperLine;
        var healthBarEnt = entity instanceof Player ? upperLine : lowerLine;

        healthBarEnt.customName(generateHealthBar(entity));
        if (customNameEnt != null) {
            if (entity.customName() == null) {
                customNameEnt.remove();
                this.upperLineArmorStands.remove(entityId);
            } else {
                customNameEnt.customName(entity.customName());
            }
        }

        if (this.cleanupTasks.containsKey(entityId)) {
            this.cleanupTasks.get(entityId).cancel();
        }

        final BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                removeIndicator(entityId);
            }
        }.runTaskLater(this.plugin, 200L); // 10秒 = 200 ticks

        this.cleanupTasks.put(entityId, task);
    }

    private void removeIndicator(final UUID entityId) {
        final ArmorStand lowerLine = this.lowerLineArmorStands.remove(entityId);
        final ArmorStand upperLine = this.upperLineArmorStands.remove(entityId);
        if (lowerLine != null) {
            lowerLine.remove();
        }
        if (upperLine != null) {
            upperLine.remove();
        }

        final BukkitTask task = this.cleanupTasks.remove(entityId);
        if (task != null) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
    }

    /**
     * ★ 変更点: 元の名前を取得・結合する処理を削除
     * エンティティの体力に基づいてハートのComponentを生成します。
     *
     * @param entity 対象のエンティティ
     * @return 体力バーのComponent
     */
    private Component generateHealthBar(final LivingEntity entity) {
        final var attribute = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) {
            return Component.text("❤".repeat(10), NamedTextColor.RED);
        }
        final double maxHealth = attribute.getValue();
        final double currentHealth = Math.max(0, entity.getHealth());
        final double healthRatio = currentHealth / maxHealth;

        final int redHearts = (int) Math.round(healthRatio * 10);
        final int grayHearts = 10 - redHearts;

        final Component redHeartsComponent = Component.text("❤".repeat(redHearts), NamedTextColor.RED);
        final Component grayHeartsComponent = Component.text("❤".repeat(grayHearts), NamedTextColor.DARK_GRAY);

        return redHeartsComponent.append(grayHeartsComponent);
    }
}