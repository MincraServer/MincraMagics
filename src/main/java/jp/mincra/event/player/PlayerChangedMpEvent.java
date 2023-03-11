package jp.mincra.event.player;

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
    private final float oldMp;
    private final float newMp;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerChangedMpEvent(Player player, float oldMp, float newMp) {
        this.player = player;
        this.oldMp = oldMp;
        this.newMp = newMp;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public float getOldMp() {
        return oldMp;
    }

    public float getNewMp() {
        return newMp;
    }
}
