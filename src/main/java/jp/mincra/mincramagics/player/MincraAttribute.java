package jp.mincra.mincramagics.player;

public enum MincraAttribute {
    MAX_MANA("max_mana");

    private final String key;

    MincraAttribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
