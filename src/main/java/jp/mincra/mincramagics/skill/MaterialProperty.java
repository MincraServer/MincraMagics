package jp.mincra.mincramagics.skill;

public class MaterialProperty {
    private final String skillId;
    private final String materialId;
    private final float cooldown;
    private final float mp;

    private float strength;

    public MaterialProperty(String materialId, String skillId, float cooldown, float mp) {
        this.cooldown = cooldown;
        this.mp = mp;
        this.materialId = materialId;
        this.skillId = skillId;
    }

    public MaterialProperty setStrength(float strength) {
        this.strength = strength;
        return this;
    }

    public float strength() {
        return strength;
    }

    public float cooldown() {
        return cooldown;
    }

    public float mp() {
        return mp;
    }

    public String materialId() {
        return materialId;
    }

    public String skillId() {
        return skillId;
    }
}
