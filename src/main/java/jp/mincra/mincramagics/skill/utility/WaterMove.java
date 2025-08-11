package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaterMove extends MagicSkill implements Listener {
    private final Map<UUID, Property> playerUUIDsToApply = new HashMap<>();
    private record Property(int duration, double speedMultiplier, int usedAt) {}

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final int level = (int) property.level();
        final int duration = level * 45 * 20; // 45 seconds in ticks
        final double speedMultiplier = 1 + level * 0.2;

        // Core functionality
        final int currentTick = Bukkit.getCurrentTick();
        final Property prop = new Property(duration, speedMultiplier, currentTick);
        playerUUIDsToApply.put(player.getUniqueId(), prop);

        // Effect and sound
        Vfx vfx = vfxManager.getVfx("bubble_pop_hexagon");
        Location playerLoc = player.getLocation();
        player.playSound(playerLoc, Sound.ENTITY_DOLPHIN_SPLASH, 2F, 1F);
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));


        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        if (!playerUUIDsToApply.containsKey(player.getUniqueId())) return;

        Location playerLoc = player.getLocation();
        if (playerLoc.getBlock().getType() != Material.WATER) return;

        // Core functionality
        Property prop = playerUUIDsToApply.get(player.getUniqueId());
        // 効果切れ
        final int currentTick = Bukkit.getCurrentTick();
        if (currentTick - prop.usedAt >= prop.duration) {
            playerUUIDsToApply.remove(player.getUniqueId());
            return;
        }

        final double yaw = player.getY();
        final double pitch = player.getPitch();
        // -10F ~ 0.6Fの範囲でy方向のVelocityを計算
        final float vVec = Math.max(Math.min((float) (-Math.tan(pitch * Math.PI / 180.0) * 0.6F), 0.6F), -10F);

        Vector vec = new Vector(-1 * Math.sin(yaw * Math.PI / 180.0) * 0.6F, vVec, Math.cos(yaw * Math.PI / 180.0) * 0.6F).multiply(prop.speedMultiplier);
        player.setVelocity(vec);

        // Effect and sound
        player.playSound(playerLoc, Sound.ENTITY_DOLPHIN_SWIM, 1F, 1F);
        player.spawnParticle(Particle.BUBBLE, playerLoc.add(0, 0.5, 0), 10, 0.43F, 0.43F, 0.43F, 1F); // offset 0.43
    }
}
