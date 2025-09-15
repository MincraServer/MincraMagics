package jp.mincra.mincramagics.gui.screen;

import com.gamingmesh.jobs.Jobs;
import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.gui.BuildContext;
import jp.mincra.mincramagics.gui.GUI;
import jp.mincra.mincramagics.gui.component.*;
import jp.mincra.mincramagics.gui.lib.GUIHelper;
import jp.mincra.mincramagics.gui.lib.Position;
import jp.mincra.mincramagics.gui.lib.Screen;
import jp.mincra.mincramagics.nbt.ArtifactNBT;
import jp.mincra.mincramagics.utils.Strings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RefinementTable extends GUI {

    @Nullable
    @Override
    protected Screen build(BuildContext context) {
        final var artifact = useState(new ItemStack(Material.AIR, 1));
        final var ore = useState(new ItemStack(Material.AIR, 1));
        final var supportItem = useState(new ItemStack(Material.AIR, 1));

        final var player = context.player();
        final var refinement = new Refinement(artifact.value(), ore.value(), supportItem.value(), player);
        final var canRefine = refinement.canRefine();

        MincraLogger.debug("RefinementTable#build: artifact: " + Strings.truncate(artifact.value()) +
                ", ore: " + Strings.truncate(ore.value()) +
                ", supportItem: " + Strings.truncate(supportItem.value()) +
                ", canRefine: " + canRefine
        );

        final Function<ItemStack, Boolean> handleArtifactPlaced = (item) -> {
            final var newNbt = ArtifactNBT.fromItem(item);
            if (newNbt == null) return false;
            artifact.set(item.clone());
            return true;
        };

        final Consumer<Boolean> handleArtifactPickedUp = (v) -> {
            artifact.set(new ItemStack(Material.AIR, 1));
        };

        final Function<ItemStack, Boolean> handleOrePlaced = (item) -> {
            if (!Refinement.isValidOre(item)) return false;
            ore.set(item.clone());
            return true;
        };

        final Consumer<Boolean> handleOrePickedUp = (v) -> {
            MincraLogger.debug("handleOrePickedUp");
            ore.set(new ItemStack(Material.AIR, 1));
        };

        final Function<ItemStack, Boolean> handleSupportItemPlaced = (item) -> {
            if (!Refinement.isValidSupportItem(item)) return false;
            supportItem.set(item.clone());
            return true;
        };

        final Consumer<Boolean> handleSupportItemPickedUp = (v) -> {
            supportItem.set(new ItemStack(Material.AIR, 1));
        };

        final Consumer<Void> handleStartRefinement = (v) -> {
            if (!canRefine) return;

            final var artifactNBT = ArtifactNBT.fromItem(artifact.value());
            final var result = refinement.startRefinement();
            if (result == null) {
                player.sendMessage("§c◆ 精錬に失敗しました。材料を確認してください。");
                return;
            }

            // decrease ore and support item amount by 1
            final var oreClone = ore.value().clone();
            if (oreClone.getAmount() <= 1) {
                ore.set(new ItemStack(Material.DEBUG_STICK, 1));
            } else {
                oreClone.setAmount(Math.max(0, oreClone.getAmount() - 1));
                ore.set(oreClone);
            }

            final var supportItemClone = supportItem.value().clone();
            if (supportItemClone.getAmount() <= 1) {
                supportItem.set(new ItemStack(Material.DEBUG_STICK, 1));
            } else {
                supportItemClone.setAmount(Math.max(0, supportItemClone.getAmount() - 1));
                supportItem.set(supportItemClone);
            }

            if (result.isSuccess()) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
                if (artifactNBT != null && !Refinement.isMaxRefineLevel(result.resultItem())) {
                    player.sendMessage("§a◆ 成功！ (+" + artifactNBT.refineLevel() + " → +" +  ArtifactNBT.fromItem(result.resultItem()).refineLevel() + ")");
                } else {
                    // broadcast max refine level message
                    final var displayName = result.resultItem().getItemMeta().displayName();
                    Bukkit.broadcast(Component.text("◆ " + player.getName() + "さんが")
                            .append(displayName == null ? Component.text(artifact.value().getType().name()) : displayName)
                            .append(Component.text(" の精錬で最大レベルに到達しました！"))
                            .color(NamedTextColor.GREEN)
                    );
                }
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0f, 1.2f);
                if (artifactNBT != null) {
                    player.sendMessage("§c◆ 失敗... (+" + artifactNBT.refineLevel() + " → +" + ArtifactNBT.fromItem(result.resultItem()).refineLevel() + ")");
                }
            }

            artifact.set(result.resultItem());
        };

        addCloseListener(event -> {
            // return items in slots to player
            final var artifactItem = artifact.value();
            if (artifactItem.getType() != Material.AIR) {
                player.getInventory().addItem(artifactItem);
            }
            final var oreItem = ore.value();
            if (oreItem.getType() != Material.AIR && !oreItem.getType().equals(Material.DEBUG_STICK)) {
                player.getInventory().addItem(oreItem);
            }
            final var supportItemItem = supportItem.value();
            if (supportItemItem.getType() != Material.AIR && !supportItemItem.getType().equals(Material.DEBUG_STICK)) {
                player.getInventory().addItem(supportItemItem);
            }
        });

        final Predicate<Integer> isModifiableSlot = slot -> (10 <= slot && slot <= 12) || slot >= 27;

        return Screen.builder()
                .title(GUIHelper.guiTitle("精錬台", "%oraxen_gui_refinement_table%", 3))
                .size(27)
                .isModifiableSlot(isModifiableSlot)
                .components(List.of(
                        Filler.builder()
                                .pos(new Position(0, 0, 9, 3))
                                .isSlotExcluded((slot) -> isModifiableSlot.test(slot) || slot == 5 || slot == 23 || slot == 16)
                                .build(),
                        ItemSlot.builder()
                                .pos(new Position(1, 1))
                                .item(artifact.value())
                                .onItemPlaced(handleArtifactPlaced)
                                .onItemPickedUp(handleArtifactPickedUp)
                                .build(),
                        ItemSlot.builder()
                                .pos(new Position(2, 1))
                                .item(ore.value())
                                .onItemPlaced(handleOrePlaced)
                                .onItemPickedUp(handleOrePickedUp)
                                .build(),
                        ItemSlot.builder()
                                .pos(new Position(3, 1))
                                .item(supportItem.value())
                                .onItemPlaced(handleSupportItemPlaced)
                                .onItemPickedUp(handleSupportItemPickedUp)
                                .build(),
                        ItemPresentation.builder()
                                .pos(new Position(5, 0))
                                .item(refinement.getSuccessItem())
                                .build(),
                        ItemPresentation.builder()
                                .pos(new Position(5, 2))
                                .item(refinement.getFailureItem())
                                .build(),
                        Button.builder()
                                .pos(new Position(7, 1))
                                .icon(Icons.transparent(
                                        canRefine
                                                ? Component.text("精錬を開始").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
                                                : Component.text("精錬できません").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                                        List.of(
                                                canRefine
                                                        ? Component.text("成功確率  ").color(NamedTextColor.WHITE)
                                                        .append(Component.text(String.format("%.2f%%", refinement.getSuccessRate() * 100)).color(NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false)
                                                        : Component.text("-").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                                        )))
                                .onClick(handleStartRefinement)
                                .build()
                ))
                .build();
    }
}

/**
 * 精錬のコアロジック
 */
class Refinement {
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

    public static record Result(boolean isSuccess, @Nullable ItemStack resultItem) {
    }

    private final ItemStack artifact;
    private final ArtifactNBT artifactNBT;
    private final ItemStack ore;
    private final ItemStack supportItem;
    private final Player player;

    public Refinement(ItemStack artifact, ItemStack ore, ItemStack supportItem, Player player) {
        this.artifact = artifact;
        this.artifactNBT = ArtifactNBT.fromItem(artifact);
        this.ore = ore;
        this.supportItem = supportItem;
        this.player = player;
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
