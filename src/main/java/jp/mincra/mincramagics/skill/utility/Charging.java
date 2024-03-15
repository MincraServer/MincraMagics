package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

public class Charging extends MagicSkill {
    @Override
    public void onTrigger(Player player, MaterialProperty property) {
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
        Vfx vfx = vfxManager.getVfx("charging");
        vfx.playEffect(vfxLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));

        AtomicReference<Float> pitch = new AtomicReference<>((float) 1);
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // Add MP
                    mPlayer.getMp().addMp(3, false);

                    // Sound
                    Location newPlayerLoc = player.getLocation().add(new Vector(0, 1, 0));
                    world.playSound(newPlayerLoc, Sound.ENTITY_PLAYER_LEVELUP, 1F, pitch.get());
                    pitch.updateAndGet(p -> p + 0.1f);
                    // Particle
                    world.spawnParticle(Particle.SCRAPE, newPlayerLoc, 10, 0.6, 0.6, 0.6, 1);
                    return true;
                })
                .repeat(TickTime.TICK, 15, 10, 3)
                .run();
    }
}
