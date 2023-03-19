package jp.mincra.mincramagics.skill;

/**
 * @param cooldown
 * @param mp
 * @param strength
 * @param materialId The name of material; not skill.
 */
public record MaterialProperty(float cooldown, int mp, String materialId,
                               int strength) {
}
