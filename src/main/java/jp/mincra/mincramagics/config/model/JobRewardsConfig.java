package jp.mincra.mincramagics.config.model;

import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Represents the configuration for a single job (e.g., "Hunter").
 * This is the top-level object for a job entry in job_reward.yml.
 *
 * @param enabled Whether rewards for this job are active.
 * @param rewards The list of level-based rewards for this job.
 */
public record JobRewardsConfig(
        boolean enabled,
        List<JobRewardConfig> rewards
) {
    /**
     * Creates a JobRewardsConfig instance from a Bukkit ConfigurationSection.
     * This is the main entry point for parsing a job's configuration block.
     *
     * @param section The ConfigurationSection representing a job (e.g., the "Hunter" section).
     * @return A new JobRewardsConfig instance.
     */
    public static JobRewardsConfig fromYaml(ConfigurationSection section) {
        if (section == null) {
            MincraLogger.warn("Configuration section is null. Returning disabled config.");
            return new JobRewardsConfig(false, Collections.emptyList());
        }

        boolean enabled = section.getBoolean("enabled", false);
        List<Map<?, ?>> rewardMaps = section.getMapList("rewards");

        List<JobRewardConfig> rewards = new ArrayList<>();
        for (Map<?, ?> rewardMap : rewardMaps) {
            rewards.add(JobRewardConfig.fromMap(rewardMap));
        }

        return new JobRewardsConfig(enabled, rewards);
    }

    /**
     * Merges this configuration with a default configuration.
     * If a level is defined in both, the current config's reward takes precedence.
     * @param def The default JobRewardsConfig to merge with.
     * @return A new JobRewardsConfig instance with merged rewards.
     */
    public JobRewardsConfig mergeDefault(JobRewardsConfig def) {
        if (def == null) return this;
        if (!def.enabled) return this;

        final Map<Integer, List<ItemStack>> mergedRewards = new HashMap<>();
        for (JobRewardConfig reward : this.rewards) {
            mergedRewards.put(reward.level(), new ArrayList<>(reward.items()));
        }
        for (JobRewardConfig reward : def.rewards) {
            if (!mergedRewards.containsKey(reward.level())) {
                mergedRewards.put(reward.level(), new ArrayList<>(reward.items()));
            }
        }
        return new JobRewardsConfig(
                this.enabled,
                mergedRewards.entrySet().stream()
                        .map(entry -> new JobRewardConfig(entry.getKey(), entry.getValue()))
                        .sorted(Comparator.comparingInt(JobRewardConfig::level))
                        .toList()
        );
    }
}

