package jp.mincra.mincramagics.config.model;

import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Represents a single item entry within a reward, responsible for creating the ItemStack.
 */
public record ItemConfig(
        String type,
        String id,
        int amount
) {
    /**
     * Creates an ItemConfig instance from a Map (parsed from YAML).
     *
     * @param map The map representing an item.
     * @return A new ItemConfig instance.
     */
    public static ItemConfig fromMap(Map<?, ?> map) {
        String type = map.containsKey("minecraft_type") ? "minecraft" : "oraxen";
        String key = type.equals("minecraft") ? "minecraft_type" : "oraxen_item";
        String id = map.containsKey(key) ? map.get(key).toString() : "";
        int amount = map.containsKey("amount") ? (int) map.get("amount") : 1;
        return new ItemConfig(type, id, amount);
    }

    /**
     * Converts this configuration object into an actual ItemStack.
     * Handles both vanilla Minecraft items and Oraxen items.
     *
     * @return An Optional containing the ItemStack if successful, otherwise an empty Optional.
     */
    public java.util.Optional<ItemStack> toItemStack() {
        if (id.isEmpty()) {
            MincraLogger.warn("Item ID is empty. Cannot create ItemStack.");
            return java.util.Optional.empty();
        }

        try {
            if ("minecraft".equals(type)) {
                Material material = Material.getMaterial(id.toUpperCase());
                if (material == null) {
                    MincraLogger.warn("Invalid Minecraft material: " + id);
                    return java.util.Optional.empty();
                }
                return java.util.Optional.of(new ItemStack(material, amount));
            } else if ("oraxen".equals(type)) {
                if (!OraxenItems.exists(id)) {
                    MincraLogger.warn("Oraxen item not found: " + id);
                    return java.util.Optional.empty();
                }
                ItemStack item = OraxenItems.getItemById(id).build();
                item.setAmount(amount);
                return java.util.Optional.of(item);
            }
        } catch (Exception e) {
            MincraLogger.fatal("Failed to create ItemStack for id '" + id + "': " + e.getMessage());
        }

        return java.util.Optional.empty();
    }
}
