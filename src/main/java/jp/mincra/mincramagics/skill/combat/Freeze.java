package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Freeze extends MagicSkill implements Listener {
    private record LocationAndOldType(Location location, Material oldType) {}

    private List<Vector> icePositions = null;

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        Location playerLoc = player.getLocation();

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector axis = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("ice");
        Location eyeLoc = player.getEyeLocation();
        double vfxAngle =  Math.toRadians(eyeLoc.getYaw());
        vfx.playEffect(vfxLoc, 5, axis, vfxAngle);

        World world = player.getWorld();

        // PlaySound
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 2);

        // 2ブロック間隔で探索する
        Vector gap = player.getLocation().getDirection().multiply(2);
        AtomicReference<Location> atomicSearchLoc = new AtomicReference<>(eyeLoc.clone().add(gap));
        UUID playerUuid = player.getUniqueId();
        float strength = property.strength();

        // 前方に地面に沿ってターゲットを探索する
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    Location searchLoc = atomicSearchLoc.updateAndGet(loc -> {
                        Location updatedLoc = loc.add(gap);

                        // 5ブロック下まで地面を探索する
                         Location groundLoc = getHighestLocationBelow(updatedLoc, 5);
                        // 地面が見つかった
                        if (groundLoc != null) {
                            updatedLoc.setY(groundLoc.getY() + 1);
                        }
                        return updatedLoc;
                    });

                    // 実行したプレイヤーは除く
                    Collection<LivingEntity> entities = searchLoc.getNearbyLivingEntities(2, e -> e.getUniqueId() != playerUuid);

                    // エンティティが見つからなかったら終わり
                    if (entities.size() == 0) {
                        vfx.playEffect(searchLoc.clone().add(0, - 0.5, 0), 5, axis, vfxAngle);
                        return true;
                    }

                    // 1体だけ
                    LivingEntity target = entities.iterator().next();
                    // 凍らせる
                    List<LocationAndOldType> placedIcePos = fillWithIce(target);
                    // 0ダメージを与えて Player を DamageSource に設定する
                    target.damage(0, player);

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
                            .delay(TickTime.SECOND, ((int) strength) * 5L)
                            .run();

                    // false を返して Tween を終える
                    return false;
                })
                .repeat(TickTime.TICK, 1, 0, ((int) strength) * 2)
                .run();

        return true;
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

    @Nullable
    private Location getHighestLocationBelow(Location location, int maxSearchY) {
        Location newLoc = location.clone();

        for (int i = 0; i < maxSearchY; i++) {
            // 空気じゃなかったらそれが HighestLocation
            if (!newLoc.getBlock().getType().isAir()) return newLoc;

            newLoc.setY(newLoc.y() - 1);
        }

        return null;
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
