package jp.mincra.mincramagics.font;

import io.th0rgal.oraxen.OraxenPlugin;
import jp.mincra.mincramagics.MincraLogger;
import me.clip.placeholderapi.PlaceholderAPI;

public class Fonts {
    public static String getMaterialFont(String materialId, boolean hud) {
        final var glyphId = (hud ? "hud_" : "") + "material_" + materialId;
        final var glyphIdWithoutLevel = glyphId.replaceAll("_[0-9]+$", "");
        final var fontManager = OraxenPlugin.get().getFontManager();

        if (fontManager.getGlyphFromID(glyphId) != null) {
            return PlaceholderAPI.setPlaceholders(null, "%oraxen_" + glyphId + "%");
        }

        if (fontManager.getGlyphFromID(glyphIdWithoutLevel) != null) {
            return PlaceholderAPI.setPlaceholders(null, "%oraxen_" + glyphIdWithoutLevel + "%");
        }

        if (fontManager.getGlyphFromID(glyphIdWithoutLevel + "_1") != null) {
            // レベル1のアイコンが登録されている場合はそれを使う
            return PlaceholderAPI.setPlaceholders(null, "%oraxen_" + glyphIdWithoutLevel + "_1%");
        }

        MincraLogger.warn("CooldownHudController: " + materialId + " and " + glyphIdWithoutLevel + " aren't registered to oraxen.");
        return PlaceholderAPI.setPlaceholders(null, "%oraxen_required%");
    }
}
