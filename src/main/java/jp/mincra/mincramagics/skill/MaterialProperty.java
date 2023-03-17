package jp.mincra.mincramagics.skill;

/**
 *
 * @param cooldown
 * @param mp
 * @param strength
 * @param materialId The name of material; not skill.
 */
public class MaterialProperty {
    private final float cooldown;
    private final int mp;
    private final  int strength;
    private final  String materialId;

    public MaterialProperty(float cooldown, int mp, int strength, String materialId) {
        this.cooldown = cooldown;
        this.mp = mp;
        this.strength = strength;
        this.materialId = materialId;
    }

    public float cooldown() {
        return cooldown;
    }

    public int mp() {
        return mp;
    }

    public int strength() {
        return strength;
    }

    public String materialId() {
        return materialId;
    }
}
