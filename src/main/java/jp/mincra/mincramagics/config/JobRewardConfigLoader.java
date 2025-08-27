package jp.mincra.mincramagics.config;


import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.config.model.JobRewardsConfig;

/**
 * A loader class responsible for parsing a YamlConfiguration
 * and creating a map of job names to their reward configurations.
 */
public class JobRewardConfigLoader {

    /**
     * Gets the loaded reward configuration for a specific job.
     *
     * @param jobName The name of the job.
     * @return The JobRewardsConfig for the given job, or null if not found.
     */
    public static JobRewardsConfig getJobConfig(String jobName) {
        final var config = MincraMagics.getConfigManager().getJobRewardConfig();
        final var jobNames = config.getKeys(false);
        if (!jobNames.contains(jobName)) {
            return null; // Job not found
        }

        final var section = config.getConfigurationSection(jobName);
        if (section == null) {
            return null; // Section not found, should not happen if jobNames is correct
        }

        return JobRewardsConfig.fromYaml(section);
    }
}
