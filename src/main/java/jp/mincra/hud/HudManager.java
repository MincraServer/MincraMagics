package jp.mincra.hud;

import jp.mincra.MincraMagics;
import jp.mincra.core.MincraPlayer;
import jp.mincra.core.PlayerManager;
import jp.mincra.event.player.PlayerChangedMpEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;

public class HudManager implements Listener {
    private PlayerManager playerManager;
    private final MpHudController mpHud = new MpHudController();

    private Collection<MincraPlayer> players;

    public HudManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        players = playerManager.getPlayers();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(MincraMagics.getInstance(), () -> {
            for (MincraPlayer mPlayer : players) {
                mpHud.displayMpBar(mPlayer.getPlayer(), mPlayer.getMp());
            }
        }, 0L, 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        players = playerManager.getPlayers();
    }

    @EventHandler
    public void onPlayerChangedMp(PlayerChangedMpEvent e) {
        mpHud.displayMpBar(e.getPlayer(), e.getMp());
    }

}
