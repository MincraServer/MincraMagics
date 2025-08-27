package jp.mincra.mincramagics.gui.lib;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.titleupdater.InventoryUpdate;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIHelper {
    public static void updateTitle(Player player, String title) {
        new BKTween(MincraMagics.getInstance())
                .delay(TickTime.TICK, 1)
                .execute(v -> {
                    // FIXME: Use Inventory.title().replaceText() instead since setTitle() is deprecated
                    player.getOpenInventory().setTitle(title);
                    // final Component component = player.getOpenInventory().title().replaceText(TextReplacementConfig.builder().replacement("/[^_]/" + title + "/g").build());
                    // player.updateInventory();
                    InventoryUpdate.updateInventory(player, title);
                    return true;
                })
                .run();
    }

    public static String guiTitle(String name, String glyphPlaceholder) {
        return Color.COLOR_WHITE +
                PlaceholderAPI.setPlaceholders(null, Glyphs.NEG_8 + glyphPlaceholder)
                + Color.COLOR_DARK_GRAY
                + PlaceholderAPI.setPlaceholders(null, Glyphs.NEG_10 + Glyphs.NEG_32 + Glyphs.NEG_128 + name);
    }

    /**
     * Get the lowest empty slot in the inventory.
     * @param inv The inventory to check.
     * @return The index of the lowest empty slot, or -1 if the inventory is full.
     */
    public static int getTopLeftEmptySlot(Inventory inv) {
        ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null || item.getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isTopLeftEmptySlot(Inventory inv, int index) {
        return getTopLeftEmptySlot(inv) == index;
    }

    public static boolean isInventoryFUll(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }
}
