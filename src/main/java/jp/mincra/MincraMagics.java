package jp.mincra;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.durability.DurabilityMechanicFactory;
import jp.mincra.command.CommandRegisterer;
import jp.mincra.core.PlayerManager;
import jp.mincra.hud.HudManager;
import jp.mincra.oraxen.mechanics.MagicStuffMechanicFactory;
import org.bukkit.Bukkit;
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

        protocolManager = ProtocolLibrary.getProtocolManager();
        playerManager = new PlayerManager();
        hudManager = new HudManager(playerManager);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(playerManager, this);
        pluginManager.registerEvents(hudManager, this);

        new CommandRegisterer().registerAll();

        Bukkit.getLogger().info("Is it updated?");
        MechanicsManager.registerMechanicFactory("magicstuff",
                MagicStuffMechanicFactory::new);
        MechanicsManager.registerMechanicFactory("durability2",
                DurabilityMechanicFactory::new);
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
