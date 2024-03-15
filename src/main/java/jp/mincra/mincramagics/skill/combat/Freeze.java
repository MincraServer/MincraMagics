package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Freeze extends MagicSkill implements Listener {
    private record LocationAndOldType(Location location, Material oldType) {}

    private List<Vector> icePositions = null;

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
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

        World world = player.getWorld();

        // PlaySound
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 2);

        // 2ブロック間隔で探索する
        Vector gap = player.getLocation().getDirection().setY(0).multiply(2);

        // 前方に地面に沿ってターゲットを探索する
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    Location searchLoc = playerLoc.add(gap);
                    // 5ブロック下まで地面を探索する
                    Location groundLoc = getHighestLocationBelow(searchLoc, 5);

                    // 地面が見つかった
                    if (groundLoc != null) {
                        searchLoc.setY(groundLoc.getY());
                    }

                    Collection<LivingEntity> entities = searchLoc.getNearbyLivingEntities(1);

                    // エンティティが見つからなかったら終わり
                    if (entities.size() == 0) return true;

                    // 1体だけ
                    LivingEntity target = entities.iterator().next();
                    // 凍らせる
                    List<LocationAndOldType> placedIcePos = fillWithIce(target);

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
                            .delay(TickTime.SECOND, 5)
                            .run();

                    // false を返して Tween を終える
                    return false;
                })
                .repeat(TickTime.TICK, 1, 0, 5)
                .run();
    }

    private List<Vector> getIcePositions() {
        if (icePositions != null) return icePositions;

        icePositions = new ArrayList<>();

        for (int y = 0; y < 5; y++) {
            int max = 2 - (int) Math.ceil((double) y / 2);

            for (int x = 0; x < max; x++) {
                for (int z = 0; z < max; z++) {
                    if (x + z > max) continue;
                    icePositions.add(new Vector(x, y, z));
                }
            }
        }

        return icePositions;
    }

    @Nullable
    private Location getHighestLocationBelow(Location location, int maxSearchY) {
        Location newLoc = location.clone();

        for (int i = 0; i < maxSearchY; i++) {
            newLoc.setY(newLoc.y() - 1);

            // 空気じゃなかったらそれが HighestLocation
            if (!newLoc.getBlock().getType().isAir()) return newLoc;
        }

        return null;
    }

    private List<LocationAndOldType> fillWithIce(LivingEntity target) {
        Location targetLoc = target.getLocation();
        List<Vector> icePositions = getIcePositions();
        List<LocationAndOldType> placedIcePos = new ArrayList<>();

        for (Vector icePos : icePositions) {
            Location placeLoc = targetLoc.add(icePos);
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
