package jp.mincra.mincramagics.player;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class SkillCooldown {
    private final Map<String, Integer> materialIdToCooldownEndTick = new HashMap<>();

    public boolean isCooldown(String materialId) {
        if (!materialIdToCooldownEndTick.containsKey(materialId)) {
            return false;
        }
        int cooldownEndTick = materialIdToCooldownEndTick.get(materialId);
        int current = Bukkit.getCurrentTick();
        boolean isCooldown = current < cooldownEndTick;
        if (!isCooldown) {
            materialIdToCooldownEndTick.remove(materialId);
        }
        return isCooldown;
    }

    /**
     *
     * @param materialId
     * @param cooldown Cooldown as second
     */
    public void setCooldown(String materialId, float cooldown) {
        if (!isCooldown(materialId)) {
            materialIdToCooldownEndTick.remove(materialId);
        }

        long current = Bukkit.getCurrentTick();
        int cooldownEndTime = (int) (current + (cooldown * 20));
        materialIdToCooldownEndTick.put(materialId, cooldownEndTime);
    }

    public Map<String, Integer> materialIdToCooldownEndTick() {
        return materialIdToCooldownEndTick;
    }
}
