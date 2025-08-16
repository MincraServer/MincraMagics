package jp.mincra.mincramagics.player;

import org.bukkit.attribute.AttributeModifier;

public class MP {
    private float mp;
    private float baseMaxMp;
    private float oldMp;
    private MP oldState;
    private MincraAttributeInstance maxManaAttribute;

    public MP(float mp, float baseMaxMp, float oldMp) {
        this.mp = mp;
        this.baseMaxMp = baseMaxMp;
        this.oldMp = oldMp;
    }

    public MP(float mp, float baseMaxMp, float oldMp, MincraAttributeInstance maxManaAttribute) {
        this.mp = mp;
        this.baseMaxMp = baseMaxMp;
        this.oldMp = oldMp;
        this.maxManaAttribute = maxManaAttribute;
    }

    public float getMp() {
        if (getMaxMp() < mp) { // FIXME: getMP() should not contain logic that modifies mp?
            mp = getMaxMp();
        }

        return mp;
    }

    public float getMaxMp() {
        double add = 0;
        double addMulti = 1.0;
        double multi = 1.0;

        for (AttributeModifier modifier : maxManaAttribute.getModifiers()) {
            final AttributeModifier.Operation operation = modifier.getOperation();
            if (operation == AttributeModifier.Operation.ADD_NUMBER) {
                add += modifier.getAmount();
            } else if (operation == AttributeModifier.Operation.ADD_SCALAR) {
                addMulti += modifier.getAmount();
            } else if (operation == AttributeModifier.Operation.MULTIPLY_SCALAR_1) {
                multi *= modifier.getAmount();
            }
        }
        return (float) (((baseMaxMp * addMulti) + add) * multi);
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

        if (!ignoreMax && this.mp > getMaxMp()) {
            this.mp = baseMaxMp;
        }
    }

    public void setBaseMaxMp(float baseMaxMp) {
        this.baseMaxMp = baseMaxMp;
    }

    public void addMp(float mp, boolean ignoreMax) {
        if (mp < 0) {
            return;
        }

        oldState = this.clone();
        this.oldMp = this.mp;
        this.mp += mp;

        // MPの最大値
        if (!ignoreMax && this.mp > getMaxMp()) {
            this.mp = getMaxMp();
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
        this.baseMaxMp = oldState.baseMaxMp;
    }

    public void setMaxManaAttribute(MincraAttributeInstance maxManaAttribute) {
        this.maxManaAttribute = maxManaAttribute;
    }

    @Override
    public MP clone() {
        // super.clone() is not used here because we want to create a new instance
        // and avoid CloneNotSupportedException
        return new MP(mp, baseMaxMp, oldMp, maxManaAttribute != null ? maxManaAttribute : null);
    }
}
