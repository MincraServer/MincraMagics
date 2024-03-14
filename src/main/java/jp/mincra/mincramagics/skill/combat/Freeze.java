package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

public class Freeze extends MagicSkill {
    final private int MAX_DISTANCE = 50;
    final private int FREEZE_TICK_DURATION = 100;

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        Entity target = player.getTargetEntity(MAX_DISTANCE);

        // ターゲットがいなければ終わり
        if (target == null) return;
        // LivingEntityじゃなければ終わり
        if (!(target instanceof LivingEntity)) return;

        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        Location playerLoc = player.getLocation();

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector axis = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("ice");
        vfx.playEffect(vfxLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));


        // 5秒鈍足
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, FREEZE_TICK_DURATION, 4, false, false, false);
        ((LivingEntity) target).addPotionEffect(potionEffect);

        // Play Sound
        AtomicReference<Float> pitch = new AtomicReference<>((float) 1);
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    Location targetLoc = target.getLocation();
                    World world = targetLoc.getWorld();
                    world.playSound(targetLoc, Sound.BLOCK_GLASS_BREAK, 1F, pitch.get());
                    pitch.updateAndGet(p -> p + 0.05f);
                })
                // spends 60 tick
                .repeat(TickTime.TICK, 1, 0, 3)
                .run();
    }
}
