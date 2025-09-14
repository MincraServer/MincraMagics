package jp.mincra.mincramagics.skill.job.hunter;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Provoke extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final float level = property.level();
        final float range = 15 * level;
        // Effect and sound
        Vfx vfx = vfxManager.getVfx(Vfx.WAX_OFF_PENTAGON);
        Location playerLoc = player.getLocation();
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_6, 3, 2);

        // Core functionality
        // set target of all nearby mobs to the player
        final var nearbyEntities = player.getNearbyEntities(range, range, range);
        nearbyEntities.stream()
                .filter(e -> e instanceof Monster)
                .map(e -> (Monster) e)
                .forEach(monster -> {
                    monster.setTarget(player);
                    // add y velocity to the mob
                    monster.setVelocity(new Vector(0, 0.5, 0));
                    // add glowing effect to the mob for 5 seconds
                    monster.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0, false, false, true));
                });

        return true;
    }
}
