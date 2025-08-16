package jp.mincra.mincramagics.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerManager implements Listener {
    private final Map<UUID, MincraPlayer> players = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        addPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getUniqueId());
    }

    @Nullable
    public MincraPlayer getPlayer(UUID id) {
        if (players.containsKey(id)) {
            return players.get(id);
        }

        Player player = Bukkit.getPlayer(UUID.fromString(id.toString()));
        if (player != null) {
            addPlayer(player);
            return players.get(id);
        }

        return null;
    }
    public Collection<MincraPlayer> getPlayers() {
        return players.values();
    }

    private void addPlayer(Player player) {
        if (!players.containsKey(player.getUniqueId())) {
            // boolean isFirstLogin = player.getFirstPlayed() == 0;
            float mp = 10; //TODO: 初期MPを設定
            float maxMp = 10; //TODO: MaxMPを装備から参照
            MincraPlayer mPlayer = new MincraPlayer(player, new MP(mp, maxMp, mp));
            players.put(player.getUniqueId(), mPlayer);
        }
    }
}
