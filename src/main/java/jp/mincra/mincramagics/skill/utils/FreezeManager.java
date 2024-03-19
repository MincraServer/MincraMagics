package jp.mincra.mincramagics.skill.utils;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FreezeManager {
    private static final FreezeManager instance = new FreezeManager();

    private FreezeManager() {}

    public static FreezeManager getInstance() {
        return instance;
    }

    private List<Vector> icePositions = null;

    private record LocationAndOldType(Location location, Material oldType) {}

    public void freeze(LivingEntity target, Player caster, int durationTick) {
        // 凍らせる
        List<LocationAndOldType> placedIcePos = fillWithIce(target);
        // 0ダメージを与えて Player を DamageSource に設定する
        target.damage(0, caster);
        World world = caster.getWorld();

        // PlaySound
        world.playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5F, 1F);

        // 氷消去
        new BKTween(MincraMagics.getInstance())
                .execute(w -> {
                    for (LocationAndOldType iceLoc : placedIcePos) {
                        iceLoc.location.getBlock().setType(iceLoc.oldType);
                    }
                    return true;
                })
                // 5秒凍結
                .delay(TickTime.TICK, durationTick)
                .run();
    }

    private List<Vector> getIcePositions() {
        if (icePositions != null) return icePositions;

        icePositions = new ArrayList<>();

        for (int y = -5; y < 5; y++) {
            int max = 2 - (int) Math.ceil((double) Math.abs(y) / 2);

            for (int x = -max; x < max + 1; x++) {
                for (int z = -max; z < max + 1; z++) {
                    if (Math.abs(x) + Math.abs(z) > max) continue;
                    icePositions.add(new Vector(x, y, z));
                }
            }
        }


        return icePositions;
    }

    private List<LocationAndOldType> fillWithIce(LivingEntity target) {
        Location targetLoc = target.getLocation();
        List<Vector> icePositions = getIcePositions();
        List<LocationAndOldType> placedIcePos = new ArrayList<>();

        for (Vector icePos : icePositions) {
            Location placeLoc = targetLoc.clone().add(icePos);
            Block block = placeLoc.getBlock();
            Material blockType = block.getType();

            // 空気か草なら氷を置く
            if (blockType.isAir() || blockType.equals(Material.SHORT_GRASS) || blockType.equals(Material.TALL_GRASS)) {
                block.setType(Material.ICE);
                placedIcePos.add(new LocationAndOldType(placeLoc, blockType));
            }
        }
        return placedIcePos;
    }
}
