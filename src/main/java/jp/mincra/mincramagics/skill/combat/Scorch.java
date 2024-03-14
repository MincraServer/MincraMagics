package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Scorch extends MagicSkill {
    final private int MAX_DISTANCE = 50;
    final private int FIRE_TICK_DURATION = 100;

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        Entity target = player.getTargetEntity(MAX_DISTANCE);

        // ターゲットがいなければ終わり
        if (target == null) return;

        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector axis = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("scorch");
        vfx.playEffect(vfxLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));

        // Sound
        world.playSound(playerLoc, Sound.BLOCK_CANDLE_EXTINGUISH, 1F, 0.9F);

        // 5秒着火
        target.setFireTicks(FIRE_TICK_DURATION);
    }
}
