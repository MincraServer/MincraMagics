package jp.mincra.mincramagics.config.model;

import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
}

