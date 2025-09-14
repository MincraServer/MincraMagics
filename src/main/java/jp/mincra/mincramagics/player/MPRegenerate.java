package jp.mincra.mincramagics.player;

import dev.geco.gsit.api.event.EntityStopSitEvent;
import dev.geco.gsit.api.event.EntitySitEvent;
import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MPRegenerate implements Listener {
    private final PlayerManager playerManager;
//    private final Map<UUID, MincraPlayer> inCombatPlayers = new HashMap<>();
    private final Map<UUID, MincraPlayer> sittingPlayers = new HashMap<>();

    public MPRegenerate(MincraMagics instance, PlayerManager playerManager) {
        this.playerManager = playerManager;

        // sitting regeneration
        new BKTween(instance)
                .execute(v -> {
                    for (MincraPlayer player : sittingPlayers.values()) {
                        player.addMp(2, false);
                    }
                    return true;
                })
                .repeat(TickTime.SECOND, 3, 0, -1)
                .run();

        // general MP regeneration
        // TODO: MP自然回復系のスキルを作る時に流用する
//        new BKTween(instance)
//                .execute(v -> {
//                    for (MincraPlayer player : playerManager.getPlayers()) {
//                        if (!inCombatPlayers.containsKey(player.getPlayer().getUniqueId())) {
//                            player.addMp(1, false);
//                        }
//                    }
//                    return true;
//                })
//                .repeat(TickTime.SECOND, 20, 0, -1)
//                .run();
    }

    @EventHandler
    private void onEntitySit(EntitySitEvent event) {
        if (event.getEntity() instanceof Player player) {
            var uuid = player.getUniqueId();
            sittingPlayers.put(uuid, playerManager.getPlayer(uuid));
        }
    }

    @EventHandler
    private void onEntityGetUp(EntityStopSitEvent event) {
        if (event.getEntity() instanceof Player player) {
            sittingPlayers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    private void regenerateOnAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        final var mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (mPlayer == null) return;

        // 攻撃MP回復. TODO: 装備品で回復量を変化
        mPlayer.addMp(0.1f, false);

        if (!(event.getEntity() instanceof LivingEntity target)) return;

        // 攻撃した相手が倒れたらMP回復
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    if (target.isDead()) {
                        mPlayer.addMp(1f, false);
                        return true;
                    }
                    return false;
                })
                .delay(TickTime.TICK, 1)
                .run();
    }

//    @EventHandler
//    private void onPlayerDamage(EntityDamageByEntityEvent event) {
//        if (event.getEntity() instanceof Player player) {
//            UUID uuid = player.getUniqueId();
//            MincraPlayer mincraPlayer = playerManager.getPlayer(uuid);
//            if (mincraPlayer != null) {
//                // Combat状態にする
//                inCombatPlayers.put(uuid, mincraPlayer);
//                // Combat状態を解除するための処理を開始
//                new BKTween(MincraMagics.getInstance())
//                        .execute(v -> {
//                            // Combat状態を解除
//                            inCombatPlayers.remove(uuid);
//                            return true;
//                        })
//                        .delay(TickTime.SECOND, 10) // 5秒後に解除
//                        .run();
//            }
//        }
//    }
}
