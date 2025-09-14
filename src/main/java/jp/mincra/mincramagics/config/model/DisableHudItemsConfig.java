package jp.mincra.mincramagics.config.model;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class DisableHudItemsConfig {

    private final Set<String> disabledOraxenIds;
    private final Set<Material> disabledMinecraftTypes;

    /**
     * プライベートコンストラクタ
     * @param disabledOraxenIds HUD表示を無効にするOraxenアイテムIDのセット
     * @param disabledMinecraftTypes HUD表示を無効にするMinecraftのマテリアルのセット
     */
    private DisableHudItemsConfig(Set<String> disabledOraxenIds, Set<Material> disabledMinecraftTypes) {
        this.disabledOraxenIds = disabledOraxenIds;
        this.disabledMinecraftTypes = disabledMinecraftTypes;
    }

    /**
     * YAMLのConfigurationSectionからDisableHudItemsConfigのインスタンスを生成します。
     *
     * @param section "disable_hud_items" リストを含むConfigurationSection
     * @return 新しいDisableHudItemsConfigのインスタンス
     */
    public static DisableHudItemsConfig fromYaml(ConfigurationSection section) {
        // nullチェック
        if (section == null) {
            return new DisableHudItemsConfig(Set.of(), Set.of());
        }

        // 高速なルックアップのためにHashSetを使用
        final Set<String> oraxenIds = new HashSet<>();
        final Set<Material> minecraftTypes = new HashSet<>();

        // "disable_hud_items"キーからマップのリストを取得
        final List<Map<?, ?>> items = section.getMapList("disable_hud_items");

        for (Map<?, ?> itemMap : items) {
            // oraxen_itemの処理
            if (itemMap.containsKey("oraxen_item")) {
                oraxenIds.add(String.valueOf(itemMap.get("oraxen_item")));
            }
            // minecraft_typeの処理
            else if (itemMap.containsKey("minecraft_type")) {
                String materialName = String.valueOf(itemMap.get("minecraft_type")).toUpperCase();
                try {
                    // 文字列からMaterialを取得
                    Material material = Material.valueOf(materialName);
                    minecraftTypes.add(material);
                } catch (IllegalArgumentException e) {
                    // 不正なマテリアル名が指定された場合はログに警告を出力
                    Logger.getLogger("MincraMagics").warning("[Config] Invalid material name in disable_hud_items: " + materialName);
                }
            }
        }

        return new DisableHudItemsConfig(oraxenIds, minecraftTypes);
    }

    /**
     * 指定されたItemStackのHUD表示を無効にするべきか判定します。
     *
     * @param item 判定対象のItemStack
     * @return HUD表示を無効にする場合はtrue, それ以外はfalse
     */
    public boolean shouldDisable(ItemStack item) {
        // アイテムが無効な場合はfalseを返す
        if (item == null || item.getType().isAir()) {
            return false;
        }

        // OraxenアイテムIDで判定
        String oraxenId = OraxenItems.getIdByItem(item);
        if (oraxenId != null && disabledOraxenIds.contains(oraxenId)) {
            return true;
        }

        // MinecraftのMaterialで判定
        return disabledMinecraftTypes.contains(item.getType());
    }
}