package jp.mincra.mincramagics.config;

import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.config.model.DisableHudItemsConfig;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * A loader class responsible for parsing the main config.yml
 * and creating a DisableHudItemsConfig instance from it.
 */
public class DisableHudItemsConfigLoader {

    /**
     * Loads and returns the Disable HUD Items configuration from the main config file.
     * <p>
     * This method parses the configuration each time it is called,
     * ensuring that it reflects any changes from a config reload.
     * </p>
     *
     * @return The loaded DisableHudItemsConfig instance.
     */
    public static DisableHudItemsConfig load(ConfigManager configManager) {
        // MincraMagicsのメインインスタンスからconfig.ymlを取得します。
        final FileConfiguration config = configManager.getMainConfig();
        // config.ymlのルートセクションをDisableHudItemsConfig.fromYamlに渡してインスタンスを生成します。
        // fromYamlメソッドが内部で "disable_hud_items" キーを探索します。
        return DisableHudItemsConfig.fromYaml(config);
    }
}