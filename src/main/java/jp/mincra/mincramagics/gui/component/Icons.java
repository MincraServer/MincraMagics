package jp.mincra.mincramagics.gui.component;

import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MincraLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Icons {
    public static ItemStack transparent(Component displayName, List<Component> lore) {
        final var invisibleItem = OraxenItems.getItemById("invisible_item");
        if (invisibleItem == null) {
            MincraLogger.warn("Oraxen item 'invisible_item' not found. Using LIGHT_GRAY_STAINED_GLASS_PANE as fallback.");
            return new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        }
        final var item = invisibleItem.build().clone();
        final var itemMeta = item.getItemMeta();
        itemMeta.displayName(displayName);
        item.setItemMeta(itemMeta);
        item.lore(lore);
        return item;
    }

    public static ItemStack transparent() {
        return transparent(Component.text(""), List.of());
    }
}
