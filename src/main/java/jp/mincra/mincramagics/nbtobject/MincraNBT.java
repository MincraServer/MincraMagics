package jp.mincra.mincramagics.nbtobject;

import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MincraNBT(List<Material> materials, List<MagicEnchantment> magicEnchantments,
                        List<MaterialFilter> materialFilters) {
    // region NamespacedKey
    public static final NamespacedKey MINCRA_MAGICS_KEY = new NamespacedKey(MincraMagics.getInstance(), "MincraMagics");
    // Material
    private static final NamespacedKey MATERIALS_KEY = new NamespacedKey(MincraMagics.getInstance(), "Materials");
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(MincraMagics.getInstance(), "Slot");
    private static final NamespacedKey ID_KEY = new NamespacedKey(MincraMagics.getInstance(), "Id");
    // MagicEnchantments
    private final static NamespacedKey MAGIC_ENCHANTMENTS_KEY = new NamespacedKey(MincraMagics.getInstance(), "MagicEnchantments");
    // MaterialFilter
    private final static NamespacedKey MATERIAL_FILTERS_KEY = new NamespacedKey(MincraMagics.getInstance(), "MaterialFilters");
    // endregion

    public ItemBuilder setTag(ItemBuilder builder) {
        ItemStack item = builder.build();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        PersistentDataContainer mincraMagicsContainer = container.getAdapterContext().newPersistentDataContainer();

        // Set Materials Container
        PersistentDataContainer[] materialContainers = new PersistentDataContainer[materials.size()];
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            PersistentDataContainer materialContainer = mincraMagicsContainer.getAdapterContext().newPersistentDataContainer();
            materialContainer.set(SLOT_KEY, PersistentDataType.STRING, material.slot());
            materialContainer.set(ID_KEY, PersistentDataType.STRING, material.id());

            materialContainers[i] = materialContainer;
        }
        mincraMagicsContainer.set(MATERIALS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY, materialContainers);
        builder.setCustomTag(MINCRA_MAGICS_KEY, PersistentDataType.TAG_CONTAINER, mincraMagicsContainer);

        // 既にbuild()したのでregen()しないとTagが付与されない
        builder.regen();
        return builder;
    }

    @Nullable
    public static MincraNBT getMincraNBT(ItemStack item) {
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

        //TODO: Implement MaterialFilters and MagicEnchantments
        return new MincraNBT(materials, null, null);
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
}
