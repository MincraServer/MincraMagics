package jp.mincra.mincramagics.skill.combat;

import com.google.common.util.concurrent.AtomicDouble;
import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;

public class Mechanics extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        if(!super.onTrigger(player, property))return false;

        // Parameters
        final float level = property.level();
        final double logLevel = Math.log(level);
        final float damagePerDist = (float) (0.5 * logLevel + 0.25F);
        final double range = 10 / (1 + Math.pow(Math.E, -level));// ロジスティック関数(L=10)

        World world = player.getLocation().getWorld();

        // Jump
        player.setVelocity(new Vector(player.getVelocity().getX(),5F,player.getVelocity().getZ()));

        // playSounds
        world.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.1F, 1F);

        // Initialize_Variable(For Repeat)
        AtomicBoolean isFalling = new AtomicBoolean(false);
        AtomicDouble currentHeight = new AtomicDouble(0F);

        // メイン処理
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    final double velY = player.getVelocity().getY();
                    if(isFalling.get()){
                        if(velY >= -0.1F) {
                            double fallDistance = currentHeight.get() - player.getLocation().getY();
                            if(fallDistance >= 10) {
                                // 落下距離が10ブロック以上で攻撃
                                player.spawnParticle(Particle.INSTANT_EFFECT, player.getLocation(), 200);
                                player.spawnParticle(Particle.INSTANT_EFFECT, player.getLocation(), 200);
                                world.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.3F, 1.75F);
                                // 範囲攻撃処理
                                for (Entity entity : player.getNearbyEntities(range, 1, range)) {
                                    if (entity instanceof Monster monster) {// モンスターのみ
                                        monster.damage(fallDistance * damagePerDist, player);
                                        world.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 0.3F, 1.75F);
                                        player.setFallDistance(0);
                                        monster.setVelocity(new Vector(entity.getVelocity().getX(), 2F, entity.getVelocity().getZ()));
                                    }
                                }
                            }
                            return false;// ループ終了
                        }
                    } else if(velY <= 3.6F){
                        // 一定の高さまで上昇したので落下させる
                        player.setVelocity(new Vector(player.getVelocity().getX(),-5F,player.getVelocity().getZ()));
                        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WITHER_SHOOT, 0.1F, 1F);
                        currentHeight.set(player.getLocation().getY());
                        isFalling.set(true);
                        player.setFallDistance(-25);// 落下ダメージを打ち消す
                    } else {
                        world.spawnParticle(Particle.FIREWORK, player.getLocation(), 5);
                    }
                    return true;
                })
                .repeat(TickTime.TICK, 1, 0, -1)
                .run();
        return true;
    }
}
