package jp.mincra;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import jp.mincra.core.PlayerManager;
import jp.mincra.hud.HudManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MincraMagics extends JavaPlugin {
    private static MincraMagics INSTANCE;

    private static ProtocolManager protocolManager;
    private static PlayerManager playerManager;
    private static HudManager hudManager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        CommandManager.register();
    }

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        playerManager = new PlayerManager();
        hudManager = new HudManager();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(playerManager, this);
        pluginManager.registerEvents(hudManager, this);
    }

    @Override
    public void onDisable() {
    }

    public static MincraMagics getInstance() {
        return INSTANCE;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }
}
