package jp.mincra.core;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class SkillCooldown {
    private final Map<String, Integer> skillIdToCooldown = new HashMap<>();

    public boolean isCooldown(String skillId) {
        if (!skillIdToCooldown.containsKey(skillId)) {
            return false;
        }
        int cooldownEndTime = skillIdToCooldown.get(skillId);
        long current = System.currentTimeMillis();
        boolean isCooldown = current < cooldownEndTime;
        if (!isCooldown) {
            skillIdToCooldown.remove(skillId);
        }
        return isCooldown;
    }

    /**
     *
     * @param skillId
     * @param cooldown Cooldown as second
     */
    public void setCooldown(String skillId, float cooldown) {
        if (!isCooldown(skillId)) {
            skillIdToCooldown.remove(skillId);
        }

        Bukkit.getLogger().warning("[MincraMagics] getCurrentTick works right??");
        long current = System.currentTimeMillis();
        int cooldownEndTime = (int) (current + (cooldown * 1000));
        skillIdToCooldown.put(skillId, cooldownEndTime);
    }
}
