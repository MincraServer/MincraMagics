package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.utils.FreezeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class Lightning extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        int level = (int) property.level();
        int lightningAmount = level * 3; // 3 lightning bolts per level
        final double extraDamage = level * 2.0; // Extra damage per lightning strike

        // Core functionality
        // 5 tick　ごとに 3 * 繰り返し回数ブロック先を中心に 2 ブロック内の最も近いエンティティに雷を落とす
        AtomicInteger repeatCount = new AtomicInteger(1);
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    final var currentRepeat = repeatCount.getAndIncrement();
                    if (currentRepeat > lightningAmount) return false; // Stop after the specified amount

                    // Calculate the target location
                    Location targetLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2 * repeatCount.get()));
                    final var tmpTargets = player.getWorld().getNearbyLivingEntities(targetLocation, 2, 2, 2, e -> !e.equals(player));
                    final var nearestEntity = tmpTargets.stream()
                            .min(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(targetLocation)));
                    if (nearestEntity.isPresent()) {
                        final var target = nearestEntity.get();
                        // Apply extra damage
                        target.damage(extraDamage, player);
                        // Strike lightning at the nearest entity
                        target.getWorld().strikeLightning(target.getLocation());
                    } else {
                        // If no entity is found, strike lightning at the target location
                        targetLocation.getWorld().strikeLightning(targetLocation);
                    }

                    return true;
                })
                .repeat(TickTime.TICK, 5, 0, lightningAmount)
                .run();

        // Effect and sound
        Vfx vfx = vfxManager.getVfx("electric_spark_hexagon");
        Location playerLoc = player.getLocation();
        player.playSound(playerLoc, Sound.ENTITY_WITHER_SHOOT, 0.15F, 1F);
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));

        return true;
    }
}
