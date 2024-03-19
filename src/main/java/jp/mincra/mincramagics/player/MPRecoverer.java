package jp.mincra.mincramagics.player;

import dev.geco.gsit.api.event.EntityGetUpSitEvent;
import dev.geco.gsit.api.event.EntitySitEvent;
import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MPRecoverer implements Listener {
    private final PlayerManager playerManager;
    private final Map<UUID, MincraPlayer> sittingPlayers;

    public MPRecoverer(MincraMagics instance, PlayerManager playerManager) {
        this.playerManager = playerManager;
        sittingPlayers = new HashMap<>();

        new BKTween(instance)
                .execute(v -> {
                    for (MincraPlayer player : sittingPlayers.values()) {
                        player.getMp().addMp(2, false);
                    }
                    return true;
                })
                .repeat(TickTime.SECOND, 1, 0, -1)
                .run();
    }

    @EventHandler
    private void onEntitySit(EntitySitEvent event) {
        if (event.getEntity() instanceof Player player) {
            var uuid = player.getUniqueId();
            sittingPlayers.put(uuid, playerManager.getPlayer(uuid));
        }
    }

    @EventHandler
    private void onEntityGetUp(EntityGetUpSitEvent event) {
        if (event.getEntity() instanceof Player player) {
            sittingPlayers.remove(player.getUniqueId());
        }
    }
}
