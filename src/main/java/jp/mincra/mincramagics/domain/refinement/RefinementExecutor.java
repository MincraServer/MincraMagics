package jp.mincra.mincramagics.domain.refinement;

import com.gamingmesh.jobs.Jobs;
import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.nbt.ArtifactNBT;
import jp.mincra.mincramagics.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 精錬のコアロジック
 */
public class RefinementExecutor {
    // Oraxen のアイテムID -> 精錬確率の一次式のy切片
    private final static Map<String, Double> ORE_SUCCESS_RATE_Y_INTERCEPT = Map.of(
            "damascus", 1.824,
            "high_purity_damascus", 1.904,
            "mithril", 1.984,
            "high_purity_mithril", 2.064,
            "orichalcum", 2.144,
            "high_purity_orichalcum", 2.224
    );
    private final static int MAX_REFINE_LEVEL = 15;

    private final ItemStack artifact;
    private final ArtifactNBT artifactNBT;
    private final ItemStack ore;
    private final ItemStack supportItem;
    private final Player player;

    public record Result(boolean isSuccess, @Nullable ItemStack resultItem) {
    }

    public RefinementExecutor(ItemStack artifact, ItemStack ore, ItemStack supportItem, Player player) {
        this.artifact = artifact;
        this.artifactNBT = ArtifactNBT.fromItem(artifact);
        this.ore = ore;
        this.supportItem = supportItem;
        this.player = player;
    }

    public static boolean isValidOre(ItemStack ore) {
        if (ore == null) return false;

        final var id = OraxenItems.getIdByItem(ore);
        if (id == null) return false;

        return ORE_SUCCESS_RATE_Y_INTERCEPT.containsKey(id);
    }

    public static boolean isValidSupportItem(ItemStack supportItem) {
        // TODO: Implement support item
        return false;
    }

    public static boolean isMaxRefineLevel(ItemStack artifact) {
        final var artifactNBT = ArtifactNBT.fromItem(artifact);
        if (artifactNBT == null) return false;
        return artifactNBT.refineLevel() >= MAX_REFINE_LEVEL;
    }

    @Nullable
    public ItemStack getSuccessItem() {
        if (artifactNBT == null) return null;
        if (artifactNBT.refineLevel() >= MAX_REFINE_LEVEL) return null;
        if (!isValidOre(ore)) return null;
        return artifactNBT.setRefineLevel(artifactNBT.refineLevel() + 1).setNBTTag(new ItemStack(artifact));
    }

    @Nullable
    public ItemStack getFailureItem() {
        if (artifactNBT == null) return null;
        if (artifactNBT.refineLevel() <= 0) return null;
        if (!isValidOre(ore)) return null;
        return artifactNBT.setRefineLevel(Math.max(0, artifactNBT.refineLevel() - 1)).setNBTTag(new ItemStack(artifact));
    }

    public boolean canRefine() {
        if (artifactNBT == null) return false;
        MincraLogger.debug("canRefine: " + artifactNBT.refineLevel());
        if (artifactNBT.refineLevel() >= MAX_REFINE_LEVEL) return false;
        MincraLogger.debug("canRefine: ore: " + Strings.truncate(ore) + ", isValidOre: " + isValidOre(ore));
        return isValidOre(ore);
    }

    @Nullable
    public Result startRefinement() {
        if (!isValidOre(ore)) {
            MincraLogger.warn("Invalid ore: " + Strings.truncate(ore));
            return null;
        }

        final var successRate = getSuccessRate();
        MincraLogger.debug("Refinement#startRefinement: successRate: " + successRate);

        if (Math.random() < successRate) {
            return new Result(true, getSuccessItem());
        } else if (Math.random() < getDegradeRate()) {
            return new Result(false, getFailureItem());
        } else {
            // no change
            return new Result(false, artifact);
        }
    }

    public double getSuccessRate() {
        final var currentLevel = artifactNBT == null ? 0 : artifactNBT.refineLevel();

        final Supplier<Integer> smithLevel = () -> {
            // if Jobs is installed
            if (Bukkit.getPluginManager().isPluginEnabled("Jobs")) {
                final var jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
                if (jobsPlayer == null) return 0;

                final var smithJob = Jobs.getJob("Smith");
                if (smithJob == null) return 0;
                final var prog = jobsPlayer.getJobProgression(smithJob);
                if (prog == null) return 0;
                return prog.getLevel();
            }

            return 0;
        };


        return Math.min(1, Math.max(
                -0.16 * currentLevel
                        + ORE_SUCCESS_RATE_Y_INTERCEPT.getOrDefault(OraxenItems.getIdByItem(ore), 0.0)
                        + smithLevel.get() * 0.001,
                0.01));
    }

    private double getDegradeRate() {
        final var luckEffect = player.getPotionEffect(PotionEffectType.LUCK);
        if (luckEffect == null) return 1.0;

        return Math.max(0.5, 1.0 - 0.05 * (luckEffect.getAmplifier() + 1));
    }
}
