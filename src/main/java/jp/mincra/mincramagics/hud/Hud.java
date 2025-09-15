package jp.mincra.mincramagics.hud;

public record Hud(
        String prefix,
        String suffix
) {
    public static Hud empty() {
        return new Hud("", "");
    }

    public static Hud prefix(String prefix) {
        return new Hud(prefix, "");
    }

    public static Hud suffix(String suffix) {
        return new Hud("", suffix);
    }
}
