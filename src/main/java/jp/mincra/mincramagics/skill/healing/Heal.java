package jp.mincra.mincramagics.skill.healing;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Heal extends MagicSkill {
    final private int MAX_DISTANCE = 10;

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        Location playerLoc = player.getLocation();

        // Play Sound
        player.playSound(playerLoc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);

        Entity maybeTarget = player.getTargetEntity(MAX_DISTANCE);
        // モンスター以外
        LivingEntity target = maybeTarget instanceof LivingEntity && !(maybeTarget instanceof Monster) ? (LivingEntity) maybeTarget : player;

        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // 回復
                    target.setHealth(Math.min(target.getHealth() + 6, target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                    Location targetLoc = target.getLocation();
                    // Play Sound
                    playerLoc.getWorld().playSound(targetLoc, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1, 1.1F);
                    // Play Vfx
                    Location vfxLoc = targetLoc.clone().add(new Vector(0, 0.5, 0));
                    Vector axis = new Vector(0, 1, 0);
                    Vfx vfx = vfxManager.getVfx("healing");
                    vfx.playEffect(vfxLoc, 5, axis, 0);
                    return true;
                })
                .delay(TickTime.TICK, 20)
                .run();

        return true;
    }
}
