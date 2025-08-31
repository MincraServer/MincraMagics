package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Wraith extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Change Gamemode
        GameMode gameMode = player.getGameMode();
        player.setGameMode(GameMode.SPECTATOR);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0);
        World world = player.getWorld();

        // Vfx
        vfxManager.getVfx("wraith")
                .playEffect(player.getLocation(), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));


        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    Location location = player.getLocation();
                    world.spawnParticle(Particle.LARGE_SMOKE, location.add(new Vector(0, 1.5, 0)).add(location.getDirection().multiply(2)),
                            30, 0.4, 1.0, 0.4, 0.2);
                    return true;
                })
                .repeat(TickTime.TICK, 2, 0, 15)
                .run();

        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    player.setGameMode(gameMode);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0);
                    vfxManager.getVfx("wraith")
                            .playEffect(player.getLocation(), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
                    return true;
                })
                .delay(TickTime.TICK, 30)
                .run();

        return true;
    }
}
