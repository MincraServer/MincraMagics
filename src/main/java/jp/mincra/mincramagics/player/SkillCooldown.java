package jp.mincra.mincramagics.player;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class SkillCooldown {
    private final Map<String, Integer> skillIdToCooldowntick = new HashMap<>();

    public boolean isCooldown(String skillId) {
        if (!skillIdToCooldowntick.containsKey(skillId)) {
            return false;
        }
        int cooldownEndTick = skillIdToCooldowntick.get(skillId);
        long current = Bukkit.getCurrentTick();
        boolean isCooldown = current < cooldownEndTick;
        if (!isCooldown) {
            skillIdToCooldowntick.remove(skillId);
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
            skillIdToCooldowntick.remove(skillId);
        }

        long current = Bukkit.getCurrentTick();
        int cooldownEndTime = (int) (current + (cooldown * 20));
        skillIdToCooldowntick.put(skillId, cooldownEndTime);
    }
}
