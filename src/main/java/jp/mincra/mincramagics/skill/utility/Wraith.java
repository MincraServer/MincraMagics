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
    public void onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        // Change Gamemode
        GameMode gameMode = player.getGameMode();
        player.setGameMode(GameMode.SPECTATOR);
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0);
        World world = player.getWorld();

        // Vfx
        vfxManager.getVfx("wraith")
                .playEffect(player.getLocation(), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));


        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    Location location = player.getLocation();
                    world.spawnParticle(Particle.SMOKE_LARGE, location.add(new Vector(0, 1.5, 0)).add(location.getDirection().multiply(2)),
                            30, 0.4, 1.0, 0.4, 0.2);
                })
                .repeat(TickTime.TICK, 2, 0, 15)
                .run();

        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    player.setGameMode(gameMode);
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0);
                    vfxManager.getVfx("wraith")
                            .playEffect(player.getLocation(), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
                })
                .delay(TickTime.TICK, 30)
                .run();
    }
}
