package jp.mincra.mincramagics.gui.impl;

import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MaterialSlot;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.gui.BuildContext;
import jp.mincra.mincramagics.gui.InventoryGUI;
import jp.mincra.mincramagics.gui.lib.*;
import jp.mincra.mincramagics.nbt.ArtifactNBT;
import jp.mincra.mincramagics.skill.MaterialManager;
import lombok.Builder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class MaterialEditor extends InventoryGUI {
    private final static String activeTitle = GUIHelper.guiTitle("マテリアル作業台", "%oraxen_gui_material_editor_activated%");
    private final static String inactiveTitle = GUIHelper.guiTitle("マテリアル作業台", "%oraxen_gui_material_editor_inactivated%");
    private static final int ARTIFACT_SLOT_INDEX = 10;
    private static final int FIRST_MATERIAL_SLOT_INDEX = 12;
    private static final int LAST_MATERIAL_SLOT_INDEX = 18;

    private final Inventory inv;
    private final MaterialManager materialManager;

    public MaterialEditor() {
        inv = Bukkit.createInventory(null, 27, Component.text(inactiveTitle));
        materialManager = MincraMagics.getMaterialManager();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Nullable
    @Override
    protected GUI build(BuildContext context) {
        final var artifact = useState(new ItemStack(Material.AIR, 1));
        final var materials = useState((Map<MaterialSlot, String>) new HashMap<MaterialSlot, String>());
        final var artifactItem = artifact.value().getType() == Material.AIR ? null : artifact.value();
        final var artifactNBT = ArtifactNBT.fromItem(artifactItem);

        MincraLogger.debug("[build()] artifactItem: " + artifact.value().getType());

        final Function<ItemStack, Boolean> handleArtifactPlaced = (item) -> {
            if (item == null) return false;
//            MincraLogger.debug("Handle artifact placed: " + item.getType());
            final var newNbt = ArtifactNBT.fromItem(item);
            if (newNbt == null) return false;
//            MincraLogger.debug("Artifact set to: " + item.getType());
            artifact.set(item.clone());
            materials.set(newNbt.getMaterialMap());
            return true;
        };

        final Consumer<Boolean> handleArtifactPickedUp = (item) -> {
//            MincraLogger.debug("Handle artifact picked up");
            artifact.set(new ItemStack(Material.AIR));
            materials.set(new HashMap<>());
        };

        final  Function<MSlotAndItem, Boolean> handleMaterialPlaced = (materialInSlot) -> {
            final ItemStack currentArtifactItem = artifact.value();
//            MincraLogger.debug("[handleMaterialPlaced] materialInSlot: " + materialInSlot + ", currentArtifactItem: " + currentArtifactItem);
            if (currentArtifactItem == null || currentArtifactItem.getType() == Material.AIR) return false;
            final var currentArtifactNBT = ArtifactNBT.fromItem(currentArtifactItem);
//            MincraLogger.debug("[handleMaterialPlaced] currentArtifactNBT: " + currentArtifactNBT);
            if (currentArtifactNBT == null) return false;

            final var slot = materialInSlot.slot();
            final var material = materialInSlot.item();

//            MincraLogger.debug("[handleMaterialPlaced] slot: " + slot + ", item: " + material);

            if (material == null) {
                currentArtifactNBT.removeMaterial(slot.getSlot());
                return false;
            }

            if (!OraxenItems.exists(material)) return false;
            String materialId = OraxenItems.getIdByItem(material);
            if (!materialManager.isRegistered(materialId)) return false;
//            MincraLogger.debug("[handleMaterialPlaced] materialId: " + materialId);
            if (!currentArtifactNBT.isAvailableMaterial(materialId)) return false;

            currentArtifactNBT.setMaterial(slot.getSlot(), materialId);
            artifact.set(currentArtifactNBT.setNBTTag(artifactItem));
//            MincraLogger.debug("[handleMaterialPlaced] new artifact item set.");
            return true;
        };

        final  Function<MaterialSlot, Boolean> handleMaterialPickedUp = (slot) -> {
            final ItemStack currentArtifactItem = artifact.value();
            MincraLogger.debug("[handleMaterialPickedUp] slot: " + slot + ", currentArtifactItem: " + currentArtifactItem);
            if (currentArtifactItem == null || currentArtifactItem.getType() == Material.AIR) return false;

            // 最新の Item から NBT を再生成する
            final ArtifactNBT currentArtifactNBT = ArtifactNBT.fromItem(currentArtifactItem);
            MincraLogger.debug("[handleMaterialPickedUp] currentArtifactNBT: " + currentArtifactNBT);
            if (currentArtifactNBT == null) return false;

            currentArtifactNBT.removeMaterial(slot.getSlot());
            MincraLogger.debug("[handleMaterialPickedUp] artifactItem: " + currentArtifactItem);
            artifact.set(currentArtifactNBT.setNBTTag(currentArtifactItem));
            return true;
        };

        addClickListener(e -> {
            // When move to player's inventory from GUI
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                    && e.getRawSlot() >= 0 && e.getRawSlot() < 27) {
                if (e.getRawSlot() == ARTIFACT_SLOT_INDEX) {
                    handleArtifactPickedUp.accept(true);
                } else {
                    handleMaterialPickedUp.apply(MaterialSlot.values()[e.getRawSlot() - FIRST_MATERIAL_SLOT_INDEX]);
                }
            }
        });

        addDragListener(IntStream.range(0, 27).boxed(), e -> {
            // Prevent dragging items in the GUI
            e.setCancelled(true);
        });

        addCloseListener(e -> {
            if (artifactItem == null) return;

            if (GUIHelper.isInventoryFUll(player)) {
                Location playerLoc = player.getLocation();
                playerLoc.getWorld().dropItem(playerLoc, artifactItem);
            } else {
                player.getInventory().addItem(artifactItem);
            }
        });

        final Predicate<Integer> isModifiableSlot = index -> index == ARTIFACT_SLOT_INDEX
                || (index >= FIRST_MATERIAL_SLOT_INDEX && index <= LAST_MATERIAL_SLOT_INDEX)
                || index >= 27; // Player's inventory
        final List<MaterialSlot> unavailableSlots = artifactNBT == null
                ? List.of()
                : Arrays.stream(MaterialSlot.values())
                        .filter(materialSlot -> !artifactNBT.availableSlots().contains(materialSlot))
                        .toList();

        return GUI.builder()
                .title(artifactItem == null ? inactiveTitle : activeTitle)
                .isModifiableSlot(isModifiableSlot)
                .components(List.of(
                        Filler.builder()
                                .pos(new Position(0, 0, 9, 3))
                                .isSlotExcluded(isModifiableSlot)
                                .build(),
                        ArtifactSlot.builder()
                                .pos(new Position(1, 1))
                                .artifact(artifactItem)
                                .onArtifactPlaced(handleArtifactPlaced)
                                .onArtifactPickedUp(handleArtifactPickedUp)
                                .build(),
                        MaterialSlots.builder()
                                .pos(new Position(3, 1, 6))
                                .materials(materials.value())
                                .unavailableSlots(unavailableSlots)
                                .onMaterialPlaced(handleMaterialPlaced)
                                .onMaterialPickedUp(handleMaterialPickedUp)
                                .build()
                ))
                .build();
    }
}

@Builder
class Filler extends GuiComponent {
    private final Position pos;
    private final Predicate<Integer> isSlotExcluded;

    public Filler(Position pos, Predicate<Integer> isSlotExcluded) {
        this.pos = pos;
        this.isSlotExcluded = isSlotExcluded;
    }

    @Override
    public void render(Inventory inv) {
        pos.toIndexStream().forEach(i -> {
            if (isSlotExcluded.test(i)) return;
            inv.setItem(i, GuiIcons.invisible);
        });
    }
}

@Builder
class ArtifactSlot extends GuiComponent {
    private final Position pos;
    private final Function<ItemStack, Boolean> onArtifactPlaced;
    private final Consumer<Boolean> onArtifactPickedUp;
    private final ItemStack artifact;

    public ArtifactSlot(Position pos, Function<ItemStack, Boolean> onArtifactPlaced, Consumer<Boolean> onArtifactPickedUp, ItemStack artifact) {
        this.pos = pos;
        this.onArtifactPlaced = onArtifactPlaced;
        this.onArtifactPickedUp = onArtifactPickedUp;
        this.artifact = artifact;
    }

    @Override
    public void render(Inventory inv) {
        final var currentItem = inv.getItem(pos.startIndex());
//        MincraLogger.debug("[ArtifactSlot] artifact: " + artifact);
        if (artifact != null && currentItem != null && currentItem.getType() != Material.AIR) {
            inv.setItem(pos.startIndex(), artifact);
        }
        addMoveToOtherInventoryListener(pos.startIndex(), (e) ->
                e.event().setCancelled(!onArtifactPlaced.apply(e.item())));
        addPlaceListener(pos.startIndex(), (e) ->
                e.event().setCancelled(!onArtifactPlaced.apply(e.item())));
        addPickupListener(pos.startIndex(), (e) ->
                onArtifactPickedUp.accept(true));
        addSwapListener(pos.startIndex(), (e) ->
                // TODO: Artifact の入れ替えに対応する
                e.event().setCancelled(true));
    }
}

@Builder
class MaterialSlots extends GuiComponent {
    private static final ItemStack unavailableSlotItem = OraxenItems.getItemById("unavailable_slot").build();
    private final Position pos;
    private final Function<MSlotAndItem, Boolean> onMaterialPlaced;
    private final Function<MaterialSlot, Boolean> onMaterialPickedUp;
    private final Map<MaterialSlot, String> materials;
    private final List<MaterialSlot> unavailableSlots;

    public MaterialSlots(Position pos, Function<MSlotAndItem, Boolean> onMaterialPlaced,
                         Function<MaterialSlot, Boolean> onMaterialPickedUp,
                         Map<MaterialSlot, String> materials, List<MaterialSlot> unavailableSlots) {
        this.pos = pos;
        this.onMaterialPlaced = onMaterialPlaced;
        this.onMaterialPickedUp = onMaterialPickedUp;
        this.materials = materials;
        this.unavailableSlots = unavailableSlots;
    }

    @Override
    public void render(Inventory inv) {
        if (materials == null) {
            Arrays.stream(MaterialSlot.values()).forEach(slot ->
                    inv.setItem(pos.startIndex() + slot.ordinal(), null));
            return;
        }

        Arrays.stream(MaterialSlot.values()).forEach(slot -> {
            // original() で slot を index にする
            final var index = pos.startIndex() + slot.ordinal();

            if (unavailableSlots.contains(slot)) {
                inv.setItem(index, unavailableSlotItem);
                addClickListener(index, e -> e.setCancelled(true));
                return;
            }

            addMoveToOtherInventoryListener(index, (e) ->
                    e.event().setCancelled(!onMaterialPlaced.apply(
                            new MSlotAndItem(slot, e.item()))));
            addPlaceListener(index, (e) ->
                    e.event().setCancelled(!onMaterialPlaced.apply(
                            new MSlotAndItem(slot, e.item()))));
            addPickupListener(index, (e) ->
                    e.event().setCancelled(!onMaterialPickedUp.apply(slot)));
            addSwapListener(index, (e) ->
                    e.event().setCancelled(!onMaterialPlaced.apply(
                            new MSlotAndItem(slot, e.item()))));

            if (!materials.containsKey(slot)) {
                inv.setItem(index, null);
                return;
            }

            if (!OraxenItems.exists(materials.get(slot))) return;

            final var materialItem = OraxenItems.getItemById(materials.get(slot)).build();

            // artifact がセットされたときだけ更新する
            // material がプレイヤ＜によって置されたときは更新しない
            inv.setItem(index, materialItem);
        });
    }
}

record MSlotAndItem(MaterialSlot slot, ItemStack item) {}
