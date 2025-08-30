package jp.mincra.mincramagics.skill.job.hunter;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Burst extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final float level = property.level();
        final float durationInTick = 20 * (60 + level * 2); // 60秒 + レベル×2秒
        final float potionLevel = Math.min(1 + (int) (level / 2), 4); // 最大レベル4

        // Effect and sound
        Vfx vfx = vfxManager.getVfx(Vfx.LAVA_HEXAGON);
        Location playerLoc = player.getLocation();
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 0.9f);

        // Core functionality
        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, (int) durationInTick, (int) potionLevel - 1, false, true, true));

        return true;
    }
}
