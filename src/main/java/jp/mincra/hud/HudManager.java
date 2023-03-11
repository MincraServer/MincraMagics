package jp.mincra.hud;

import jp.mincra.event.player.PlayerChangedMpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HudManager implements Listener {
    private final MpHudController mpHud = new MpHudController();

    @EventHandler
    public void onPlayerChangedMp(PlayerChangedMpEvent e) {
        mpHud.displayMpBar(e.getPlayer(), e.getMp());
    }
}
