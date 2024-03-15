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
    public void onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        Location playerLoc = player.getLocation();

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector axis = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("charging");
        vfx.playEffect(vfxLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));

        // Play Sound
        player.playSound(playerLoc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);

        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // 最大で6までしか回復しない
                    if (mPlayer.getMp().getMp() < 6) {
                        // Add MP
                        mPlayer.getMp().addMp(1, false);
                    }
                    return true;
                })
                // spends 60 tick
                .repeat(TickTime.TICK, 10, 0, 6)
                .run();
    }
}
