package jp.mincra.mincramagics.skill;

public class MaterialProperty {
    private final String skillId;
    private final String materialId;
    private final float cooldown;
    private final float mp;

    private float level;

    public MaterialProperty(String materialId, String skillId, float cooldown, float mp, float level) {
        this.cooldown = cooldown;
        this.mp = mp;
        this.materialId = materialId;
        this.skillId = skillId;
        this.level = level;
    }

    public MaterialProperty setLevel(float level) {
        this.level = level;
        return this;
    }

    public float strength() {
        return level;
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
