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

    /**
     * @param name             The name of the GUI.
     * @param glyphPlaceholder The placeholder for the glyph to be used as an icon.
     * @param height           The height of the GUI in rows (1-6).
     * @return The formatted title string.
     */
    public static String guiTitle(String name, String glyphPlaceholder, int height) {
        return Color.COLOR_WHITE +
                PlaceholderAPI.setPlaceholders(null, Glyphs.NEG_8 + glyphPlaceholder)
                + Color.COLOR_DARK_GRAY
                // if height is
                // - 1: NEG_244 in total
                // - 2: NEG_220 in total
                // - 3: NEG_196 in total
                // - 4: NEG_170 in total
                // - 5: NEG_144 in total
                // - 6: NEG_128 in total
                + PlaceholderAPI.setPlaceholders(null, switch (height) {
                    // FIXME: Use a loop to generate the string instead of hardcoding it
            case 1 -> Glyphs.NEG_128 + Glyphs.NEG_64 + Glyphs.NEG_32 + Glyphs.NEG_16 + Glyphs.NEG_4;
            case 2 -> Glyphs.NEG_128 + Glyphs.NEG_64 + Glyphs.NEG_16 + Glyphs.NEG_8 + Glyphs.NEG_4;
            case 3 -> Glyphs.NEG_128 + Glyphs.NEG_64 + Glyphs.NEG_4;
            case 4 -> Glyphs.NEG_128 + Glyphs.NEG_32 + Glyphs.NEG_8 + Glyphs.NEG_2;
            case 5 -> Glyphs.NEG_128 + Glyphs.NEG_16 + Glyphs.NEG_4;
            case 6 -> Glyphs.NEG_128 + Glyphs.NEG_8;
            default -> Glyphs.NEG_128 + Glyphs.NEG_64;
        } + name);
    }

    /**
     * Get the lowest empty slot in the inventory.
     *
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
