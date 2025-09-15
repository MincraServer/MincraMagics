package jp.mincra.mincramagics.skill.utils;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class Paralyze {
    private final JavaPlugin plugin;

    public Paralyze(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 対象を一定時間麻痺 (NoAI) させる
     * @param target 麻痺させる対象
     * @param durationTicks 麻痺させる時間 (tick)
     */
    public void execute(LivingEntity target, int durationTicks) {
        target.setAI(false);
        // ANGRY_VILLAGER (location: x, y + eyeHeight, z), count: 12, offsetX: 0.33, offsetY: 0.26, offsetZ: 0.33, extra: 1
        target.getLocation().getWorld().spawnParticle(Particle.ANGRY_VILLAGER, target.getLocation().add(0, target.getEyeHeight(), 0), 12, 0.33F, 0.26F, 0.33F, 1);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> target.setAI(true), durationTicks);
    }
}
