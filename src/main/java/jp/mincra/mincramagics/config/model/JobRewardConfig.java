package jp.mincra.mincramagics.config.model;

import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a single reward entry, containing a level and a list of items.
 *
 * @param level The required level to receive this reward.
 * @param items The list of ItemStacks to be given as a reward.
 */
public record JobRewardConfig(
        int level,
        List<ItemStack> items
) {
    /**
     * Creates a JobRewardConfig instance from a Map (parsed from YAML).
     *
     * @param map The map representing a reward entry.
     * @return A new JobRewardConfig instance.
     */
    public static JobRewardConfig fromMap(Map<?, ?> map) {
        int level = map.containsKey("level") ? (int) map.get("level") : 1;

        List<Map<?, ?>> itemMaps = map.containsKey("items") ? (List<Map<?, ?>>) map.get("items") : null;
        if (itemMaps == null) {
            MincraLogger.warn("'items' list not found for level " + level);
            return new JobRewardConfig(level, Collections.emptyList());
        }

        List<ItemStack> items = new ArrayList<>();
        for (Map<?, ?> itemMap : itemMaps) {
            ItemConfig.fromMap(itemMap).toItemStack().ifPresent(items::add);
        }

        return new JobRewardConfig(level, items);
    }
}
