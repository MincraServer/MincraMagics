package jp.mincra.mincramagics.player;

import jp.mincra.mincramagics.core.MP;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * プレイヤーのMPが変化したときに呼び出されます。
 */
public class PlayerChangedMpEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final MP mp;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerChangedMpEvent(Player player, MP mp) {
        this.player = player;
        this.mp = mp;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public MP getMp() {
        return mp;
    }
}
