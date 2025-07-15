package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.utils.FreezeManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Freeze extends MagicSkill implements Listener {
    private FreezeManager freezeManager = null;

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

                    if (freezeManager == null) freezeManager = FreezeManager.getInstance();
                    freezeManager.freeze(target, player, (int) strength * 100);

                    // false を返して Tween を終える
                    return false;
                })
                .repeat(TickTime.TICK, 1, 0, ((int) strength) * 2)
                .run();

        return true;
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
}
