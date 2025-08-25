package jp.mincra.mincramagics.gui.lib;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.titleupdater.InventoryUpdate;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

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
}
