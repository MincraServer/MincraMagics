package jp.mincra.core;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener {
    private final Map<UUID, MincraPlayer> players = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        boolean isFirst = player.getFirstPlayed() == 0;
        float mp = isFirst ? 20 : 20; //TODO: 既にログインしたことのあるプレイヤーは、一番最後にログインしていた時のMPを代入
        float maxMp = 20; //TODO: MaxMPを装備から参照
        MincraPlayer mPlayer = new MincraPlayer(player, new MP(mp, maxMp, mp));
        players.put(player.getUniqueId(), mPlayer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getUniqueId());
    }

    public MincraPlayer getPlayer(UUID id) {
        return players.get(id);
    }
}
