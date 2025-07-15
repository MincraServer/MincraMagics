package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Charge extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        Location playerLoc = player.getLocation();

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector axis = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("charging");
        vfx.playEffect(vfxLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));

        // Play Sound
        player.playSound(playerLoc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());

        int strength = (int) property.strength();
        final int maxMp = strength * 7;

        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // 10まで回復
                    if (mPlayer.getMp() < maxMp) {
                        // Add MP
                        mPlayer.addMp(1, false);
                    }
                    return true;
                })
                // spends 60 tick
                .repeat(TickTime.TICK, 10 / strength, 0, maxMp)
                .run();

        return true;
    }
}
