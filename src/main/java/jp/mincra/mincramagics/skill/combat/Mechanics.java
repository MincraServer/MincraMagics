package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Mechanics extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        if(!super.onTrigger(player, property))return false;

        // LevelGet
        int skillLevel = (int) property.level();

        final float fallDamagePer = switch (skillLevel) {
            case 1 -> 0.25F;
            case 2 -> 1F;
            default -> 1F;
        };

        World world = player.getLocation().getWorld();

        // Jump
        player.setVelocity(new Vector(player.getVelocity().getX(),5F,player.getVelocity().getZ()));

        // PlaySounds
        world.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.1F, 1F);

        // Initialize_Variable(For Repeat)
        var ref = new Object() {
            boolean exit = false;
            double height = 0;
        };

        // メイン処理
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    if(ref.exit){
                        if(player.getVelocity().getY()>=-0.1F) {
                            double fallDistance = ref.height - player.getLocation().getY();// 落下距離
                            if(fallDistance >= 10) {
                                // 落下距離が10ブロック以上で攻撃
                                player.spawnParticle(Particle.INSTANT_EFFECT, player.getLocation(), 200);
                                player.spawnParticle(Particle.INSTANT_EFFECT, player.getLocation(), 200);
                                world.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.3F, 1.75F);
                                // 範囲攻撃処理
                                for (Entity entity : player.getNearbyEntities(7, 1, 7)) {
                                    if (entity instanceof Monster) {// モンスターのみ
                                        ((Damageable) entity).damage(fallDistance * fallDamagePer);
                                        world.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 0.3F, 1.75F);
                                        player.setFallDistance(0);
                                        entity.setVelocity(new Vector(entity.getVelocity().getX(), 2F, entity.getVelocity().getZ()));
                                    }
                                }
                            }
                            return false;// ループ終了
                        }
                    } else if(player.getVelocity().getY() <= 3.6F){
                        // 一定の高さまで上昇したので落下させる
                        player.setVelocity(new Vector(player.getVelocity().getX(),-5F,player.getVelocity().getZ()));
                        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_WITHER_SHOOT, 0.1F, 1F);
                        ref.height = player.getLocation().getY();
                        ref.exit = true;
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
