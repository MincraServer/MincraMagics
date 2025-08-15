package jp.mincra.mincramagics.skill;

import java.util.Map;

public class MaterialProperty {
    private final String skillId;
    private final String materialId;
    private final float cooldown;
    private final float mp;
    private final float level;
    private final Map<String, Object> extra;


    public MaterialProperty(String materialId, String skillId, float cooldown, float mp, float level, Map<String, Object> extra) {
        this.cooldown = cooldown;
        this.mp = mp;
        this.materialId = materialId;
        this.skillId = skillId;
        this.level = level;
        this.extra = extra;
    }

    public float level() {
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

    public Map<String, Object> extra() {
        return extra;
    }
}
