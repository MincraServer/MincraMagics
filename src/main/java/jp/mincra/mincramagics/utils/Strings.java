package jp.mincra.mincramagics.utils;

public class Strings {
    public static String truncate(Object obj, int maxLength) {
        if (obj == null) return null;
        final var str = obj.toString();
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    public static String truncate(Object obj) {
        return truncate(obj, 32);
    }
}
