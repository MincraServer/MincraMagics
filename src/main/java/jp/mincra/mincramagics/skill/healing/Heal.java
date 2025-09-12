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
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {

        // Parameters
        final double level = property.level();
        final double healAmount = 6.0f * level; // 6 HP per level
        final double range = 5.0f * level;

        Location playerLoc = player.getLocation();
        final var nearbyPlayers = playerLoc.getWorld().getNearbyEntities(playerLoc, range, range, range, e -> e instanceof Player && !e.equals(player));

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage("§f◆ 回復の効果範囲内に他のプレイヤーがいません。");
            return false;
        }

        if (!super.onTrigger(player, property)) return false;

        // Play Sound
        player.getWorld().playSound(playerLoc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);

        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    for (final Entity entity : nearbyPlayers) {
                        if (entity instanceof LivingEntity target && !(entity instanceof Monster)) {
                            // 回復
                            target.setHealth(Math.min(target.getHealth() + healAmount, target.getAttribute(Attribute.MAX_HEALTH).getValue()));
                            Location targetLoc = target.getLocation();
                            // Play Sound
                            playerLoc.getWorld().playSound(targetLoc, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1, 1.1F);
                            // Play Vfx
                            Location vfxLoc = targetLoc.clone().add(new Vector(0, 0.5, 0));
                            Vector axis = new Vector(0, 1, 0);
                            Vfx vfx = vfxManager.getVfx("healing");
                            vfx.playEffect(vfxLoc, 5, axis, 0);
                        }
                    }
                    return true;
                })
                .delay(TickTime.TICK, 20)
                .run();

        return true;
    }
}
