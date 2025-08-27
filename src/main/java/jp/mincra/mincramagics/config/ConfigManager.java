package jp.mincra.mincramagics.config;

import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and accessing configuration files for the plugin.
 * Designed to be extensible for multiple configuration files.
 */
public class ConfigManager {
    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> configs = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    /**
     * Loads all necessary configuration files.
     * To add a new config file in the future, simply add another call to loadConfigFile().
     */
    public void loadConfigs() {
        loadConfigFile("config.yml");
        loadConfigFile("job_reward.yml");
    }

    /**
     * Loads a single configuration file from the plugin's data folder.
     * If the file doesn't exist, it's copied from the plugin's resources.
     *
     * @param fileName The name of the configuration file (e.g., "config.yml").
     */
    private void loadConfigFile(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);

        // If the file doesn't exist in the data folder, save the default from resources
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
            MincraLogger.info(String.format("'%s' not found, created a default one.", fileName));
        }

        // Load the configuration from the file and store it in the map
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(fileName, config);
        MincraLogger.info(String.format("Successfully loaded '%s'.", fileName));
    }

    /**
     * Gets a loaded configuration by its file name.
     *
     * @param fileName The name of the file (e.g., "config.yml").
     * @return The YamlConfiguration object, or null if not found.
     */
    public YamlConfiguration getConfig(String fileName) {
        return configs.get(fileName);
    }

    /**
     * A convenience method to get the main 'config.yml'.
     *
     * @return The main YamlConfiguration. Returns null if 'config.yml' was not loaded.
     */
    public YamlConfiguration getMainConfig() {
        return getConfig("config.yml");
    }

    public YamlConfiguration getJobRewardConfig() {
        return getConfig("job_reward.yml");
    }

    /**
     * Reloads all configuration files from disk.
     */
    public void reloadConfigs() {
        // Clear existing configurations
        configs.clear();
        // Load all configs again
        loadConfigs();
        MincraLogger.info("All configuration files have been reloaded.");
    }
}
