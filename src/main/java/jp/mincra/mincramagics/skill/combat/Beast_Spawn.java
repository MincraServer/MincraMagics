package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class BeastSpawn extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return false;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        final Wolf wolf;

        World world = player.getLocation().getWorld();

        wolf=(Wolf) world.spawnEntity(player.getLocation(),EntityType.WOLF);
        world.spawnParticle(Particle.INSTANT_EFFECT, wolf.getLocation(), 1);//SPELL_INSTANTからINSTANT_EFFECTへ

        wolf.customName(Component.text("幻獣"));//setCustomNameは非推奨なのでcustomNameを使用
        wolf.setCollarColor(DyeColor.GREEN);
        wolf.setAdult();
        wolf.setOwner(player);
        wolf.setBreed(false);

        // 召喚した幻獣は200ティックで消える
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    world.spawnParticle(Particle.INSTANT_EFFECT, wolf.getLocation(), 1);//SPELL_INSTANTからINSTANT_EFFECTへ
                    wolf.remove();
                    return true;
                })
                .delay(TickTime.TICK, 200)
                .run();

        return true;
    }
}
