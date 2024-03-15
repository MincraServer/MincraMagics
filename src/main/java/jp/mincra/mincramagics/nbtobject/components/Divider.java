package jp.mincra.mincramagics.nbtobject.components;

import java.util.Comparator;
import java.util.List;

public class Divider {
    private static final String COLOR_DARK_GRAY = "§8";
    private Divider() {}

    public static String toString(List<String> lore) {
        int descriptionLength = lore.size() > 0
                ? lore.stream().sorted(Comparator.comparingInt(String::length)).toList().get(0).length()
                : 6;
        return COLOR_DARK_GRAY + "･".repeat((int)(descriptionLength * 4.4545));
    }
}
