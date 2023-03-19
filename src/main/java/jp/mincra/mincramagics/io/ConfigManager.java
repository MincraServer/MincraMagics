package jp.mincra.mincramagics.io;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final File dataFolder;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
//        loadConfig("vfx.yml");
    }

    private File loadConfig(String path) {
        File file = new File(dataFolder, path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(path, false);
        }
        return file;
    }
}
