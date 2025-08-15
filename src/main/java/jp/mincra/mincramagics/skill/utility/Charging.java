package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.utils.Functions;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Stream;

public class Charging extends MagicSkill implements Listener {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final float level = property.level();
        final float normalizedLevel = level == 0 ? 1 : level; // Avoid division by zero
        // level1 = 6mp, level2 = 10mp
        final float mpUpperBound = (float) (8 * Math.log(normalizedLevel) + 6);
        // level1 = 10, level2 = 12, level3 = 14
        final float mpToAdd = 2 * level + 8;
        // level1 = 3sec, level2 = 2sec, level3 = 1sec
        final long castingTimeInTick = (long) (Functions.logistic(level, 2, -3, 2) * 20);
        // MP add method
        final String mpUpdateMethod = property.extra().getOrDefault("mp_update_method", "set").toString();

        if (Stream.of("add", "set").noneMatch(mpUpdateMethod::equals)) {
            MincraMagics.getPluginLogger().warning("Invalid mp_update_method: " + mpUpdateMethod + ". Valid options are 'add' or 'set'.");
            return false;
        }

        // Effect and sound
        Vfx vfx = vfxManager.getVfx("mana_charge");
        Location playerLoc = player.getLocation();
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        player.playSound(playerLoc, Sound.ITEM_TRIDENT_RETURN, 2F, 2F);

        // Core functionality
        new BKTween(MincraMagics.getInstance())
                .delay(TickTime.TICK, castingTimeInTick - 30)
                .execute(v -> {
                    // level に応じて pitch が下がる
                    double pitch = Math.random() * 0.1 + 2.0 - (level * 0.1f);
                    player.playSound(playerLoc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2F, (float) pitch);
                    vfx.playEffect(player.getLocation().add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
                    return true;
                })
                .delay(TickTime.TICK, 30)
                .execute(v -> {
                    MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
                    if (mPlayer == null) {
                        player.sendMessage("§cプレイヤー情報が見つかりません。");
                        return false;
                    }

                    double currentMp = mPlayer.getMp();
                    MincraMagics.getPluginLogger().info("Current MP: " + currentMp + ", Max MP: " + mPlayer.getMaxMp() + ", MP Upper Bound: " + mpUpperBound + ", MP to Add: " + mpToAdd + ", MP Update Method: " + mpUpdateMethod + ", castingTimeInTick: " + castingTimeInTick);
                    if (currentMp >= mPlayer.getMaxMp() || (mpUpdateMethod.equals("set") && currentMp >= mpUpperBound)) return true;

                    // Update MP based on the method
                    if (mpUpdateMethod.equals("add")) {
                        mPlayer.addMp(mpToAdd, false);
                    } else {
                        mPlayer.setMp(mpUpperBound);
                    }

                    return true;
                })
                .run();

        return true;
    }
}
