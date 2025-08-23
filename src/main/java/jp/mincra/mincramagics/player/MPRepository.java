package jp.mincra.mincramagics.player;

import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * MP を永続化する
 */
public class MPRepository implements Listener {
    private final PlayerManager playerManager;
    private final int PRECISION = 100; // MPの精度を100倍する

    public MPRepository(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final Objective objective = getOrCreateObjective();
        final Score score = objective.getScoreFor(player);
        final float currentMp = score.isScoreSet () ? (float) score.getScore() / PRECISION : 10;

        final MincraPlayer mPlayer = playerManager.getPlayer(uuid);
        if (mPlayer == null) {
            MincraLogger.warn("MincraPlayer not found for player: " + player.getName());
            return;
        }
        mPlayer.setMp(currentMp, false);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final MincraPlayer mPlayer = playerManager.getPlayer(uuid);

        if (mPlayer == null) {
            MincraLogger.warn("MincraPlayer not found for player: " + player.getName());
            return;
        }

        final Objective objective = getOrCreateObjective();
        final Score score = objective.getScoreFor(player);
        score.setScore((int) (mPlayer.getMp() * PRECISION));
    }

    private Objective getOrCreateObjective() {
        final ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getMainScoreboard();
        String OBJECTIVE_NAME = "current_mp";
        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = board.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, Component.text("Current MP"));
        }
        return objective;
    }
}
