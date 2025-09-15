package jp.mincra.mincramagics.font;

import io.th0rgal.oraxen.OraxenPlugin;
import jp.mincra.mincramagics.MaterialSlot;
import jp.mincra.mincramagics.MincraLogger;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Fonts {
    private final static Map<Integer, String> shiftCache = new HashMap<>();
    private final static Map<Integer, String> negShiftCache = new HashMap<>();
    private final static Map<String, String> materialCache = new HashMap<>();
    private final static Map<String, String> slotCache = new HashMap<>();

    public static void reload() {
        shiftCache.clear();
        negShiftCache.clear();
        materialCache.clear();
        slotCache.clear();
    }

    public static String shift(int number) {
        if (shiftCache.containsKey(number)) return shiftCache.get(number);

        final var glyph = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_" + number + "%");
        shiftCache.put(number, glyph);
        return glyph;
    }

    public static String negShift(int number) {
        if (negShiftCache.containsKey(number)) return negShiftCache.get(number);

        final var glyph = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_" + number + "%");
        negShiftCache.put(number, glyph);
        return glyph;
    }

    public static String material(String materialId, boolean hud) {
        if (materialCache.containsKey(materialId + hud)) return materialCache.get(materialId + hud);

        final Supplier<String> func = () -> {
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
        };

        final var glyph = func.get();
        materialCache.put(materialId + hud, glyph);
        return glyph;
    }

    public static String slot(MaterialSlot slot, boolean hud) {
        if (slotCache.containsKey(slot.toString() + hud)) return slotCache.get(slot.toString() + hud);

        final var prefix = hud ? "_hud" : "";
        final var glyph = switch (slot) {
            case LEFT -> PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_1%%oraxen" + prefix + "_mouse_left%%oraxen_shift_1%");
            case RIGHT -> PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_1%%oraxen" + prefix + "_mouse_right%%oraxen_shift_1%");
            case SWAP -> PlaceholderAPI.setPlaceholders(null, "%oraxen" + prefix + "_key_f%");
            case DROP -> PlaceholderAPI.setPlaceholders(null, "%oraxen" + prefix + "_key_q%");
            case PASSIVE1 -> PlaceholderAPI.setPlaceholders(null, "%oraxen" + prefix + "_passive_1%");
            case PASSIVE2 -> PlaceholderAPI.setPlaceholders(null, "%oraxen" + prefix + "_passive_2%");
        };
        slotCache.put(slot.toString() + hud, glyph);
        return glyph;
    }
}
