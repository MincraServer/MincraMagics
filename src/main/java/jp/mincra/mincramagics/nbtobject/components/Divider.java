package jp.mincra.mincramagics.nbtobject.components;

import jp.mincra.mincramagics.constant.Color;

import java.util.Comparator;
import java.util.List;

public class Divider {
    private Divider() {}

    public static String toString(List<String> lore) {
        int descriptionLength = !lore.isEmpty()
                ? lore.stream().sorted(Comparator.comparingInt(String::length)).toList().get(0).length()
                : 6;
        return Color.COLOR_DARK_GRAY + "･".repeat((int)(descriptionLength * 4.4545));
    }
}
