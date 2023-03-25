package jp.mincra.mincramagics.skill.utility;

import de.tr7zw.nbtapi.NBTEntity;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class Jump extends MagicSkill implements Listener {

    private final List<UUID> castedPlayer = new ArrayList<>();
    private final Map<UUID, Integer> soundInterval = new HashMap<>();

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        // Jump
        Vector velocity = player.getVelocity();
        if (velocity.getY() > 0) {
            velocity.setY(1.3);
        } else {
            velocity.setY(0.3);
        }
        player.setVelocity(velocity);
        castedPlayer.add(player.getUniqueId());

        // Sound
        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75F, 1F);

        // Vfx
        Vector axis = new Vector(0, 1, 0);
        vfxManager.getVfx("jump")
                .playEffect(playerLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (castedPlayer.contains(uuid)) {
            if (new NBTEntity(player).getBoolean("OnGround")) {
                // if on ground
                castedPlayer.remove(uuid);
                player.stopSound(Sound.ITEM_ELYTRA_FLYING);
                soundInterval.remove(uuid);
                return;
            }

            if (player.isSneaking()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 4, 1, false, false, false));

                int currentTick = Bukkit.getServer().getCurrentTick();
                if (!soundInterval.containsKey(uuid) || soundInterval.get(uuid) < currentTick) {
                    player.playSound(player, Sound.ITEM_ELYTRA_FLYING, 0.5f, 1);
                    soundInterval.put(uuid, 160 + currentTick);
                }
            }
        }
    }
}
