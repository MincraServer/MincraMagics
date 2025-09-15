package jp.mincra.mincramagics.skill.job.magician;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Fire extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final float level = property.level();
        final float fireDurationTicks = level * 80; // 2 seconds per level
        final double radius = 3.0 + level * 2.0; // Radius of effect
        final double knockbackMultiplier = 1.0 + level * 0.01; // Knockback strength
        final double damage = level * 3.0; // Damage per second

        // Core functionality
        final var targets = player.getWorld().getNearbyLivingEntities(player.getLocation(), radius, radius * 0.5, radius, e -> !e.equals(player));
        for (var target : targets) {
            target.damage(damage, player);
            target.setFireTicks((int) fireDurationTicks);
            // 視線方向にノックバック
            final var knockBackVec = player.getLocation().getDirection().setY(0).normalize().setY(1.5).normalize().multiply(knockbackMultiplier);
            target.setVelocity(target.getVelocity().add(knockBackVec));
        }

        // Effects and sound
        final var playerLoc = player.getLocation();
        MincraMagics.getVfxManager().getVfx(Vfx.FLAME_HEXAGON).playEffect(
                playerLoc.add(0, 0.5, 0), radius, new org.bukkit.util.Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw())
        );
        player.getWorld().playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 1F, 1F);

        return true;
    }
}
