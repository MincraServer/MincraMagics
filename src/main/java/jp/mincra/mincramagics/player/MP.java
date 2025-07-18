package jp.mincra.mincramagics.player;

public class MP {
    private float mp;
    private float maxMp;
    private float oldMp;
    private MP oldState;

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

    public boolean isEnoughMP(float requiredMp) {
        return mp >= requiredMp;
    }

    public void setMp(float mp, boolean ignoreMax) {
        oldState = this.clone();
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

        oldState = this.clone();
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
        oldState = this.clone();
        this.oldMp = this.mp;
        this.mp -= mp;

        if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public void undoLastChange() {
        this.mp = oldState.mp;
        this.oldMp = oldState.oldMp;
        this.maxMp = oldState.maxMp;
    }

    @Override
    public MP clone() {
        // super.clone() is not used here because we want to create a new instance
        // and avoid CloneNotSupportedException
        return new MP(mp, maxMp, oldMp);
    }
}
