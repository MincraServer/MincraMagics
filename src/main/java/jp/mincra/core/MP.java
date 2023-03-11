package jp.mincra.core;

public class MP {
    private float mp;
    private float maxMp;
    private float oldMp;

    public MP(float mp, float maxMp, float oldMp) {
        this.mp = mp;
        this.maxMp = maxMp;
        this.oldMp = oldMp;
    }

    public float getMp() {
        return mp;
    }

    public float getMaxMp() {
        return maxMp;
    }

    public float getOldMp() {
        return oldMp;
    }

    public void setMp(float mp, boolean ignoreMax) {
        this.oldMp = this.mp;
        this.mp = mp;

        if (!ignoreMax && this.mp > maxMp) {
            this.mp = maxMp;
        }
    }

    public void setMaxMp(float maxMp) {
        this.maxMp = maxMp;
    }

    public void addMp(float mp, boolean ignoreMax) {
        if (mp < 0) {
            return;
        }

        this.oldMp = this.mp;
        this.mp += mp;

        // MPの最大値
        if (!ignoreMax && this.mp > maxMp) {
            this.mp = maxMp;
        }
    }

    public void subMp(float mp) {
        if (mp < 0) {
            return;
        }
        this.oldMp = this.mp;
        this.mp -= mp;

        if (this.mp < 0) {
            this.mp = 0;
        }
    }
}
