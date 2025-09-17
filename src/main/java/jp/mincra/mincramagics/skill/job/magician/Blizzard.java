package jp.mincra.mincramagics.skill.job.magician;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.utils.Frostbite;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Blizzard extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final float level = property.level();
        final int freezeDurationTicks = (int) (level * 20) * 10; // 10 seconds per level
        final double damagePerSec = level * 2.0; // Damage per second
        final double radius = 2.0 + level * 2.0; // Radius of effect

        // Effects and sound
        final var playerLoc = player.getLocation();
        final var vfx = MincraMagics.getVfxManager().getVfx(Vfx.SNOWFLAKE_HEXAGON);
        player.getWorld().playSound(playerLoc, Sound.ENTITY_BREEZE_IDLE_AIR, 1F, 2F);
        // 毎ティック 範囲内に SNOWFLAKE を表示
        new BKTween(MincraMagics.getInstance())
                .execute(() -> {
                    playerLoc.getWorld().spawnParticle(org.bukkit.Particle.SNOWFLAKE, playerLoc.clone().add(0, 2, 0), (int) radius * 5, 5, radius / 4, radius / 2, 0.01);
                    return true;
                })
                .repeat(TickTime.TICK, 1, 0, freezeDurationTicks)
                .run();

        // Core functionality
        final var frostbite = new Frostbite(MincraMagics.getInstance(), 1, damagePerSec);
        final var vfxLoc = playerLoc.add(0, 0.2, 0);
        new BKTween(MincraMagics.getInstance())
                .execute(() -> {
                    final var targets = playerLoc.getWorld().getNearbyLivingEntities(playerLoc, radius, radius * 0.5, radius, e -> !e.equals(player) &&
                                    // DamageIndicator 対象外を除外
                                    !(e instanceof ArmorStand) && !(e instanceof Player))
                            .stream().distinct().toList();
                    MincraLogger.debug("Blizzard tick: applying frostbite to targets: " + targets.stream().map(CommandSender::getName).toList());
                    for (var target : targets) {
                        frostbite.execute(player, target);
                        vfx.playEffect(
                                vfxLoc, radius * 2, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw())
                        );
                    }
                    return true;
                })
                .repeat(TickTime.TICK, 10, 0, freezeDurationTicks / 10)
                .run();
        new BKTween(MincraMagics.getInstance())
                .execute(() -> {
                    playerLoc.getWorld().playSound(playerLoc, Sound.ITEM_ELYTRA_FLYING, 1F, 2F);
                    return true;
                })
                .repeat(TickTime.SECOND, 5, 0, freezeDurationTicks / 20 / 5)
                .run();

        return true;
    }
}
