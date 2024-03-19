package jp.mincra.mincramagics.event;

import jp.mincra.mincramagics.player.MP;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * プレイヤーのMPが変化したときに呼び出されます。
 */
public class PlayerMpChangedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final MP mp;
    private boolean cancelled = false;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerMpChangedEvent(Player player, MP mp) {
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (!cancelled) {
            cancelled = cancel;
            mp.undoLastChange();
        }
    }
}
