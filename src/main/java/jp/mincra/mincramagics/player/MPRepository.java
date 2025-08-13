package jp.mincra.mincramagics.player;

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
    private final Logger logger;
    private final String OBJECTIVE_NAME = "current_mp";
    private final int PRESITION = 100; // MPの精度を100倍する

    public MPRepository(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.logger = MincraMagics.getPluginLogger();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final Objective objective = getOrCreateObjective();
        final Score score = objective.getScoreFor(player);
        final float currentMp = (float) score.getScore() / PRESITION;

        final MincraPlayer mPlayer = playerManager.getPlayer(uuid);
        if (mPlayer == null) {
            logger.warning("MincraPlayer not found for player: " + player.getName());
            return;
        }
        mPlayer.setMp(currentMp, true);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final MincraPlayer mPlayer = playerManager.getPlayer(uuid);

        if (mPlayer == null) {
            logger.warning("MincraPlayer not found for player: " + player.getName());
            return;
        }

        final Objective objective = getOrCreateObjective();
        final Score score = objective.getScoreFor(player);
        score.setScore((int) (mPlayer.getMp() * PRESITION));
    }

    private Objective getOrCreateObjective() {
        final ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getMainScoreboard();
        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = board.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, Component.text("Current MP"));
        }
        return objective;
    }
}
