package jp.mincra.core;

import jp.mincra.event.player.PlayerChangedMpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class MincraPlayer {
    private final Player player;
    private final MP mp;
    private final PluginManager pluginManager;

    public MincraPlayer(Player player, MP mp) {
        this.player = player;
        this.mp = mp;
        pluginManager = Bukkit.getPluginManager();
    }

    public MP getMp() {
        return mp;
    }

    /**
     * MPを足します
     * @param mp 差分のMP
     * @param ignoreMax MPの最大値を無視するか. デフォルトはfalse
     */
    public void addMp(float mp, boolean ignoreMax) {
        this.mp.addMp(mp, ignoreMax);
        // イベントの呼び出し
        PlayerChangedMpEvent e = new PlayerChangedMpEvent(player, this.mp);
        pluginManager.callEvent(e);
    }

    public void addMp(float mp) {
        addMp(mp, false);
    }

    /**
     * MPを引きます
     * @param mp 差分のMP
     */
    public void subMp(float mp) {
        this.mp.subMp(mp);

        PlayerChangedMpEvent e = new PlayerChangedMpEvent(player, this.mp);
        pluginManager.callEvent(e);
    }

    /**
     * MPを入力値に設定する。MpMaxの上限はない。
     * @param mp 書き換えるMP
     * @param ignoreMax MPの最大値を無視するか. デフォルトはtrue
     */
    public void setMp(float mp, boolean ignoreMax) {
        this.mp.setMp(mp, ignoreMax);

        PlayerChangedMpEvent e = new PlayerChangedMpEvent(player, this.mp);
        pluginManager.callEvent(e);
    }

    public void setMp(float mp) {
        setMp(mp, true);
    }

    public Player getPlayer() {
        return player;
    }
}
