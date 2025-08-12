package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.potion.PotionEffectType;

public class BeastSpawn extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        if (!super.onTrigger(player, property)) return false;

        // LevelGet
        int skillLevel = (int) property.level();

        final Wolf[] wolf = new Wolf[4];
        final int SpawnCount = switch (skillLevel) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 5;
            default -> 1;
        };

        World world = player.getLocation().getWorld();

        for (int i = 0; SpawnCount > i; i++) {
            wolf[i] = (Wolf) world.spawnEntity(player.getLocation(), EntityType.WOLF);
            world.spawnParticle(Particle.INSTANT_EFFECT, wolf[i].getLocation(), 1);//SPELL_INSTANTからINSTANT_EFFECTへ
            wolf[i].customName(Component.text("幻獣"));//setCustomNameは非推奨なのでcustomNameを使用
            switch (i) {
                case 1:
                    wolf[i].setCollarColor(DyeColor.GREEN);
                    break;
                case 2:
                    wolf[i].setCollarColor(DyeColor.BLUE);
                    break;
                case 3:
                    wolf[i].setCollarColor(DyeColor.YELLOW);
                    break;
                case 4:
                    wolf[i].setCollarColor(DyeColor.ORANGE);
                    break;
                case 5:
                    wolf[i].setCollarColor(DyeColor.RED);
                    break;
            }
            wolf[i].setAdult();
            wolf[i].setOwner(player);
            wolf[i].setBreed(false);

            // バフを付与する
            int duration = 200 / 20;// 200ティック分(1秒20ティックらしい)
            wolf[i].addPotionEffect(PotionEffectType.HEALTH_BOOST.createEffect(duration, skillLevel + 2));
            wolf[i].addPotionEffect(PotionEffectType.STRENGTH.createEffect(duration, skillLevel + 1));
            wolf[i].addPotionEffect(PotionEffectType.INSTANT_HEALTH.createEffect(duration, 99));
        }

        // 召喚した幻獣は200ティックで消える
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    for(int i = 0; SpawnCount > i; i++) {
                        world.spawnParticle(Particle.INSTANT_EFFECT, wolf[i].getLocation(), 1);//SPELL_INSTANTからINSTANT_EFFECTへ
                        wolf[i].remove();
                    }
                    return true;
                })
                .delay(TickTime.TICK, 200)
                .run();

        return true;
    }
}
