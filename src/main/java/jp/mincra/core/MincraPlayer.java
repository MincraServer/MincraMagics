package jp.mincra.core;

import jp.mincra.event.player.PlayerChangedMpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class MincraPlayer {
    private final Player player;
    private float mp;
    private PluginManager pluginManager;

    public MincraPlayer(Player player, float mp) {
        this.player = player;
        this.mp = mp;
        pluginManager = Bukkit.getPluginManager();
    }

    public float getMp() {
        return mp;
    }

    /**
     * MPを足します
     * @param mp 差分のMP
     */
    public void addMp(float mp) {
        float oldMp = this.mp;
        this.mp += mp;

        // イベントの呼び出し
        PlayerChangedMpEvent e = new PlayerChangedMpEvent(player, oldMp, this.mp);
        pluginManager.callEvent(e);
    }

    /**
     * MPを引きます
     * @param mp 差分のMP
     */
    public void subMp(float mp) {
        float oldMp = this.mp;
        this.mp -= mp;

        PlayerChangedMpEvent e = new PlayerChangedMpEvent(player, oldMp, this.mp);
        pluginManager.callEvent(e);
    }

    public void setMp(float mp) {
        float oldMp = this.mp;
        this.mp = mp;

        PlayerChangedMpEvent e = new PlayerChangedMpEvent(player, oldMp, this.mp);
        pluginManager.callEvent(e);
    }

    public Player getPlayer() {
        return player;
    }
}
