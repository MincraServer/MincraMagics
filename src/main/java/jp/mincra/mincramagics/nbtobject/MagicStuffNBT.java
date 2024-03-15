package jp.mincra.mincramagics.nbtobject;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbtobject.components.Divider;
import jp.mincra.mincramagics.nbtobject.pdc.PersistentDataTypeEx;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
 *   - Strength
 *   - Accuracy
 *   - MPSaver
 *   - CooldownShorter
 * @param materials
 * @param magicEnchantments
 * @param materialFilters
 * @param descriptionLore
 */
public record MagicStuffNBT(List<Material> materials,
                            List<MagicEnchantment> magicEnchantments,
                            List<MaterialFilter> materialFilters,
                            List<String> descriptionLore) {
    // region NamespacedKey
    private static final JavaPlugin mincra = MincraMagics.getInstance();
    private static final NamespacedKey MINCRA_MAGICS_KEY = new NamespacedKey(mincra, "MincraMagics");
    // Material
    private static final NamespacedKey MATERIALS_KEY = new NamespacedKey(mincra, "Materials");
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(mincra, "Slot");
    private static final NamespacedKey ID_KEY = new NamespacedKey(mincra, "Id");
    // MagicEnchantments
    private final static NamespacedKey MAGIC_ENCHANTMENTS_KEY = new NamespacedKey(mincra, "MagicEnchantments");
    // MaterialFilter
    private final static NamespacedKey MATERIAL_FILTERS_KEY = new NamespacedKey(mincra, "MaterialFilters");
    private final static NamespacedKey LORE_KEY = new NamespacedKey(mincra, "LORE");
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
    private static final String COLOR_GRAY = "§7";
    private static final String COLOR_WHITE = "§f";
    // endregion

    public ItemBuilder setNBTTag(ItemBuilder builder) {
        ItemStack item = builder.build();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        PersistentDataContainer newMincraMagicsContainer = container.getAdapterContext().newPersistentDataContainer();

        // Set Materials Container
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

        //// Set Lore
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
                materialName = materialItemBuilder.getDisplayName();
            } else {
                materialName = materialId;
            }

            newLore.add(COLOR_WHITE + SLOT_TO_GLYPH.get(material.slot) + SHIFT_2 +
                    getMaterialGlyph(materialId) + SHIFT_1 + materialName);
        }

        // Loreの横線2
        newLore.add(divider);

        builder.setLore(newLore);

        // 既にbuild()したのでregen()しないとTagが付与されない
        builder.regen();
        return builder;
    }

    @Nullable
    public static MagicStuffNBT getMincraNBT(ItemStack item) {
        if (item == null) return null;

        PersistentDataContainer mincramagicsCon = item.getItemMeta().getPersistentDataContainer()
                .get(MINCRA_MAGICS_KEY, PersistentDataType.TAG_CONTAINER);

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
        String[] loreInNBT = mincramagicsCon.get(LORE_KEY, PersistentDataTypeEx.STRING_ARRAY);
        if (loreInNBT != null) {
            defaultLore = Arrays.stream(loreInNBT).toList();
        } else {
            defaultLore = new ArrayList<>();
        }

        //TODO: Implement MaterialFilters and MagicEnchantments
        return new MagicStuffNBT(materials,
                null,
                null,
                defaultLore);
    }

    /**
     *
     * @return (K,V) = (Slot, Id)
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
            case "left": return 0;
            case "right": return 1;
            case "swap": return 2;
            case "drop": return 3;
        }
        return 4;
    }
}
