package jp.mincra.bktween;

public enum TickTime {
    TICK(1),
    SECOND(20),
    MINUTE(1200),
    HOUR(72000);

    private final int multi;

    TickTime(int multi) {
        this.multi = multi;
    }

    public int getMultiplier() {
        return multi;
    }
}
