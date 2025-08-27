package jp.mincra.mincramagics.skill;

import java.util.Map;

public record MaterialProperty(String materialId, String skillId, float cooldown, float mp, float level,
                               Map<String, Object> extra) {
}
