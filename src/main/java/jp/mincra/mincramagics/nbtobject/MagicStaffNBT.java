package jp.mincra.mincramagics.nbtobject;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MaterialSlot;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.mincramagics.nbtobject.components.Divider;
import jp.mincra.mincramagics.nbtobject.utils.PDCUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MincraMagics
 * - Materials
 * - MaterialFilters
 * - MagicEnchantments
 * - DescriptionLore
 * - MagicStuffProperty
 * - Strength
 * - Accuracy
 * - MPSaver
 * - CooldownShorter
 *
 * @param materials
 * @param descriptionLore
 */
public record MagicStaffNBT(List<Material> materials,
                            List<MaterialSlot> availableSlots,
                            List<String> availableMaterials,
                            List<String> descriptionLore) {
    // region Oraxen Tag
    private static final Map<String, String> SLOT_TO_GLYPH = Map.of(
            "left", PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_1%%oraxen_mouse_left%%oraxen_shift_1%"),
            "right", PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_1%%oraxen_mouse_right%%oraxen_shift_1%"),
            "swap", PlaceholderAPI.setPlaceholders(null, "%oraxen_key_f%"),
            "drop", PlaceholderAPI.setPlaceholders(null, "%oraxen_key_q%")
    );
    private static final String SHIFT_1 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_1%");
    private static final String SHIFT_2 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_2%");
    private static final Map<String, String> MATERIAL_ID_TO_GLYPH = new HashMap<>();

    private static String getMaterialGlyph(String materialId) {
        if (MATERIAL_ID_TO_GLYPH.containsKey(materialId)) {
            return MATERIAL_ID_TO_GLYPH.get(materialId);
        }
        String glyph = PlaceholderAPI.setPlaceholders(null, "%oraxen_material_" + materialId + "%");
        MATERIAL_ID_TO_GLYPH.put(materialId, glyph);
        return glyph;
    }
    // endregion

    public ItemBuilder setNBTTag(ItemBuilder builder) {
        ItemStack item = builder.build();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        PersistentDataContainer mincraMagicsContainer = container.getAdapterContext().newPersistentDataContainer();

        /* Set Custom NBT Tag */
        List<PersistentDataContainer> materialContainers = new ArrayList<>(materials.size());
        for (Material material : materials) {
            PersistentDataContainer materialContainer = mincraMagicsContainer.getAdapterContext().newPersistentDataContainer();
            materialContainer.set(NamespacedKeys.SLOT_KEY, PersistentDataType.STRING, material.slot());
            materialContainer.set(NamespacedKeys.ID_KEY, PersistentDataType.STRING, material.id());

            materialContainers.add(materialContainer);
        }
        mincraMagicsContainer.set(NamespacedKeys.MATERIALS_KEY, PersistentDataType.LIST.dataContainers(), materialContainers);

        // Set lore tag
        PDCUtils.setStrings(mincraMagicsContainer, NamespacedKeys.LORE_KEY, descriptionLore);

        // Set available slots and available materials tag
        PDCUtils.setStrings(mincraMagicsContainer, NamespacedKeys.AVAILABLE_MATERIALS_KEY, availableMaterials);
        PDCUtils.setStrings(mincraMagicsContainer, NamespacedKeys.AVAILABLE_SLOTS_KEY,
                availableSlots.stream().map(MaterialSlot::getSlot).toList());

        // Finalize the MincraMagics container
        builder.setCustomTag(NamespacedKeys.MINCRA_MAGICS_KEY, PersistentDataType.TAG_CONTAINER, mincraMagicsContainer);

        /* Set Lore */
        List<String> newLore = new ArrayList<>(descriptionLore);

        // Loreの横線
        String divider = Divider.toString(descriptionLore);
        newLore.add(divider);

        // 各スロットに装備されたマテリアルのリスト
        // 表示順に並べ替え
        List<Material> sortedMaterials = materials.stream()
                .sorted(Comparator.comparingInt(a -> MaterialSlot.getIndexOf(a.slot)))
                .toList();
        for (Material material : sortedMaterials) {
            String materialId = material.id;
            ItemBuilder materialItemBuilder = OraxenItems.getItemById(materialId);
            String materialName;
            if (materialItemBuilder != null) {
                materialName = materialItemBuilder.getItemName();
            } else {
                materialName = materialId;
            }

            newLore.add(Color.COLOR_WHITE + SLOT_TO_GLYPH.get(material.slot) + SHIFT_2 +
                    getMaterialGlyph(materialId) + SHIFT_1 + materialName);
        }

        // Loreの横線2
        newLore.add(divider);

        builder.setLore(newLore);

        // 既にbuild()したので clearItemStack()しないとTagが付与されない
        builder.clearItemStack();
        return builder;
    }

    @Nullable
    public static MagicStaffNBT getMincraNBT(ItemStack item) {
        if (item == null) return null;

        final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        PersistentDataContainer mincramagicsCon = container.get(NamespacedKeys.MINCRA_MAGICS_KEY, PersistentDataType.TAG_CONTAINER);

        if (mincramagicsCon == null) return null;
        PersistentDataContainer[] materialsCon = mincramagicsCon.get(NamespacedKeys.MATERIALS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY);
        if (materialsCon == null) return null;

        // Get Materials
        List<Material> materials = new ArrayList<>();
        for (PersistentDataContainer materialCon : materialsCon) {
            String slot = materialCon.get(NamespacedKeys.SLOT_KEY, PersistentDataType.STRING);
            String id = materialCon.get(NamespacedKeys.ID_KEY, PersistentDataType.STRING);
            if (slot == null || id == null) continue;
            materials.add(new Material(slot.toLowerCase(), id));
        }

        // Get Lore
        List<String> loreInNBT = PDCUtils.getStrings(mincramagicsCon, NamespacedKeys.LORE_KEY);

        // Get Available Slots
        List<String> availableSlotsInNBT = PDCUtils.getStrings(mincramagicsCon, NamespacedKeys.AVAILABLE_SLOTS_KEY);
        List<MaterialSlot> availableSlots = availableSlotsInNBT == null ? new ArrayList<>() : availableSlotsInNBT.stream().map(MaterialSlot::fromString)
                .filter(Optional::isPresent).map(Optional::get).toList();
        // Available Materials
        List<String> availableMaterialsInNBT = PDCUtils.getStrings(mincramagicsCon, NamespacedKeys.AVAILABLE_MATERIALS_KEY);
        List<String> availableMaterials = availableMaterialsInNBT == null ? new ArrayList<>() : availableMaterialsInNBT.stream().toList();

        //TODO: Implement MaterialFilters and MagicEnchantments
        return new MagicStaffNBT(materials, availableSlots, availableMaterials, loreInNBT);
    }

    /**
     * @return (K, V) = (Slot, Id)
     */
    public Map<String, String> getMaterialMap() {
        return materials.stream()
                .collect(Collectors.toMap(material -> material.slot, material -> material.id));
    }

    public void setMaterial(String slot, String id) {
        removeMaterial(slot);
        materials.add(new Material(slot, id));
    }

    public void removeMaterial(String slot) {
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            if (material.slot.equals(slot)) {
                materials.remove(i);
            }
        }
    }

    public boolean isAvailableSlot(MaterialSlot slot) {
        // 空であれば全てのスロットが使用可能
        return availableSlots.isEmpty() || availableSlots.contains(slot);
    }

    public boolean isAvailableMaterial(String materialId) {
        // 空であれば全てのマテリアルが使用可能
        return availableMaterials.isEmpty() || availableMaterials.contains(materialId);
    }
}
