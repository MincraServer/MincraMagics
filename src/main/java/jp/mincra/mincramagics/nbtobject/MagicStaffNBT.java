package jp.mincra.mincramagics.nbtobject;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.mincramagics.nbtobject.components.Divider;
import jp.mincra.mincramagics.nbtobject.pdc.PersistentDataTypeEx;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
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
    // region NamespacedKey
    private static final JavaPlugin mincra = MincraMagics.getInstance();
    // FIXME: MincraMagics key may be unnecessary, as the prefix 'mincramagics:' is added to every NBT key.
    private static final NamespacedKey MINCRA_MAGICS_KEY = new NamespacedKey(mincra, "MincraMagics");
    // Material
    private static final NamespacedKey MATERIALS_KEY = new NamespacedKey(mincra, "Materials");
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(mincra, "Slot");
    private static final NamespacedKey ID_KEY = new NamespacedKey(mincra, "Id");
    private static final NamespacedKey LORE_KEY = new NamespacedKey(mincra, "LORE");
    private static final NamespacedKey AVAILABLE_SLOTS_KEY = new NamespacedKey(mincra, "AvailableSlots");
    private static final NamespacedKey AVAILABLE_MATERIALS_KEY = new NamespacedKey(mincra, "AvailableMaterials");
    // endregion

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

        PersistentDataContainer newMincraMagicsContainer = container.getAdapterContext().newPersistentDataContainer();

        /* Set Custom NBT Tag */
        PersistentDataContainer[] materialContainers = new PersistentDataContainer[materials.size()];
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            PersistentDataContainer materialContainer = newMincraMagicsContainer.getAdapterContext().newPersistentDataContainer();
            materialContainer.set(SLOT_KEY, PersistentDataType.STRING, material.slot());
            materialContainer.set(ID_KEY, PersistentDataType.STRING, material.id());

            materialContainers[i] = materialContainer;
        }
        newMincraMagicsContainer.set(MATERIALS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY, materialContainers);
        builder.setCustomTag(MINCRA_MAGICS_KEY, PersistentDataType.TAG_CONTAINER, newMincraMagicsContainer);

        // Set lore tag
        builder.setCustomTag(LORE_KEY, PersistentDataTypeEx.STRING_ARRAY, descriptionLore.toArray(new String[0]));

        // Set available slots and available materials tag
        builder.setCustomTag(AVAILABLE_SLOTS_KEY, PersistentDataTypeEx.STRING_ARRAY,
                availableSlots.stream().map(MaterialSlot::getSlot).toArray(String[]::new));
        builder.setCustomTag(AVAILABLE_MATERIALS_KEY, PersistentDataTypeEx.STRING_ARRAY,
                availableMaterials.toArray(new String[0]));

        /* Set Lore */
        List<String> newLore = new ArrayList<>(descriptionLore);

        // Loreの横線
        String divider = Divider.toString(descriptionLore);
        newLore.add(divider);

        // 各スロットに装備されたマテリアルのリスト
        // 表示順に並べ替え
        List<Material> sortedMaterials = materials.stream()
                .sorted((a, b) -> getSlotOrder(a.slot) - getSlotOrder(b.slot))
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
        PersistentDataContainer mincramagicsCon = container.get(MINCRA_MAGICS_KEY, PersistentDataType.TAG_CONTAINER);

        if (mincramagicsCon == null) return null;
        PersistentDataContainer[] materialsCon = mincramagicsCon.get(MATERIALS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY);
        if (materialsCon == null) return null;

        // Get Materials
        List<Material> materials = new ArrayList<>();
        for (PersistentDataContainer materialCon : materialsCon) {
            String slot = materialCon.get(SLOT_KEY, PersistentDataType.STRING);
            String id = materialCon.get(ID_KEY, PersistentDataType.STRING);
            if (slot == null || id == null) continue;
            materials.add(new Material(slot.toLowerCase(), id));
        }

        // Get Lore
        List<String> defaultLore;
        String[] loreInNBT = container.get(LORE_KEY, PersistentDataTypeEx.STRING_ARRAY);
        if (loreInNBT != null) {
            defaultLore = Arrays.stream(loreInNBT).toList();
        } else {
            defaultLore = new ArrayList<>();
        }

        // Get Available Slots
        String[] availableSlotsInNBT = container.get(AVAILABLE_SLOTS_KEY, PersistentDataTypeEx.STRING_ARRAY);
        List<MaterialSlot> availableSlots = availableSlotsInNBT == null ? new ArrayList<>() : Arrays.stream(availableSlotsInNBT).map(MaterialSlot::fromString)
                .filter(Optional::isPresent).map(Optional::get).toList();
        // Available Materials
        String[] availableMaterialsInNBT = container.get(AVAILABLE_MATERIALS_KEY, PersistentDataTypeEx.STRING_ARRAY);
        List<String> availableMaterials = availableMaterialsInNBT == null ? new ArrayList<>() : Arrays.stream(availableMaterialsInNBT).toList();

        //TODO: Implement MaterialFilters and MagicEnchantments
        return new MagicStaffNBT(materials, availableSlots, availableMaterials, defaultLore);
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

    private int getSlotOrder(String slot) {
        switch (slot) {
            case "left":
                return 0;
            case "right":
                return 1;
            case "swap":
                return 2;
            case "drop":
                return 3;
        }
        return 4;
    }
}
