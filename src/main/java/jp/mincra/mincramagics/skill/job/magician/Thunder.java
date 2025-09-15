package jp.mincra.mincramagics.skill.job.magician;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.utils.Paralyze;
import jp.mincra.mincramagics.skill.utils.StrikeLightning;
import jp.mincra.mincramagics.utils.Functions;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Thunder extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        // Parameters
        final float level = property.level();
        final double paralyzeDurationTicks = level * 20; // 20 ticks (1 second) per level
        final double damage = level * 8.0; // Extra damage per lightning strike
        final double radius = Functions.logistic(level, 25, 0.5, 0); // Horizontal radius of effect
        final double yRadius = 2.0 * level * 0.5; // Vertical radius of effect

        // Core functionality
        final var targets = player.getWorld().getNearbyLivingEntities(player.getLocation(), radius, yRadius, radius, e -> !e.equals(player));

        if (targets.isEmpty()) {
            player.sendMessage("§c◆ 対象が存在しない！");
            return false;
        }

        if (!super.onTrigger(player, property)) return false;

        targets.forEach(target -> {
            final var knockBackVec = target.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize().multiply(0.5 + level * 0.3);
            target.setVelocity(target.getVelocity().add(knockBackVec));
            new StrikeLightning(MincraMagics.getInstance()).execute(player, target, damage, StrikeLightning.Mode.EFFECT_ONLY);
            new Paralyze(MincraMagics.getInstance()).execute(target, (int) paralyzeDurationTicks);
        });

        // Effects and sound
        MincraMagics.getVfxManager().getVfx(Vfx.WAX_ON_PENTAGON).playEffect(
                player.getLocation().add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw())
        );

        return true;
    }
}
