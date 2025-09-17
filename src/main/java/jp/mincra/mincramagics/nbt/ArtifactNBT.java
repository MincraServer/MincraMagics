package jp.mincra.mincramagics.nbt;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MaterialSlot;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.mincramagics.domain.refinement.RefinementEffect;
import jp.mincra.mincramagics.font.Fonts;
import jp.mincra.mincramagics.nbt.components.Divider;
import jp.mincra.mincramagics.nbt.utils.PDCUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Artifact (魔法武器, 魔法杖) 用の NBT オブジェクト.
 */
public record ArtifactNBT(List<Material> materials,
                          List<MaterialSlot> availableSlots,
                          List<String> availableMaterials,
                          List<String> descriptionLore,
                          int refineLevel) {
    private static final JavaPlugin mincra = MincraMagics.getInstance();

    private static final NamespacedKey MINCRA_MAGICS_KEY = new NamespacedKey(mincra, "MincraMagics");

    private static final NamespacedKey MATERIALS_KEY = new NamespacedKey(mincra, "Materials");
    private static final NamespacedKey SLOT_KEY = new NamespacedKey(mincra, "Slot");
    private static final NamespacedKey ID_KEY = new NamespacedKey(mincra, "Id");
    private static final NamespacedKey LORE_KEY = new NamespacedKey(mincra, "LORE");
    private static final NamespacedKey AVAILABLE_SLOTS_KEY = new NamespacedKey(mincra, "AvailableSlots");
    private static final NamespacedKey AVAILABLE_MATERIALS_KEY = new NamespacedKey(mincra, "AvailableMaterials");
    private static final NamespacedKey REFINE_LEVEL_KEY = new NamespacedKey(mincra, "RefineLevel");

    public ArtifactNBT(List<Material> materials,
                       List<MaterialSlot> availableSlots,
                       List<String> availableMaterials,
                       List<String> descriptionLore) {
        this(materials, availableSlots, availableMaterials, descriptionLore, 0);
    }

    public ItemStack setNBTTag(ItemStack item) {
        return setNBTTag(new ItemBuilder(item)).build();
    }

    public ItemBuilder setNBTTag(ItemBuilder builder) {
        ItemStack item = builder.build();

        final Consumer<Void> setPDC = (v) -> {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

            PersistentDataContainer mincraMagicsContainer = container.getAdapterContext().newPersistentDataContainer();

            /* Set Custom NBT Tag */
            List<PersistentDataContainer> materialContainers = new ArrayList<>(materials.size());
            for (Material material : materials) {
                PersistentDataContainer materialContainer = mincraMagicsContainer.getAdapterContext().newPersistentDataContainer();
                materialContainer.set(SLOT_KEY, PersistentDataType.STRING, material.slot());
                materialContainer.set(ID_KEY, PersistentDataType.STRING, material.id());

                materialContainers.add(materialContainer);
            }
            mincraMagicsContainer.set(MATERIALS_KEY, PersistentDataType.LIST.dataContainers(), materialContainers);

            // Set lore tag
            PDCUtils.setStrings(mincraMagicsContainer, LORE_KEY, descriptionLore);

            // Set available slots and available materials tag
            PDCUtils.setStrings(mincraMagicsContainer, AVAILABLE_MATERIALS_KEY, availableMaterials);
            PDCUtils.setStrings(mincraMagicsContainer, AVAILABLE_SLOTS_KEY,
                    availableSlots.stream().map(MaterialSlot::getSlot).toList());
            mincraMagicsContainer.set(REFINE_LEVEL_KEY, PersistentDataType.INTEGER, refineLevel);

            // Finalize the MincraMagics container
            builder.setCustomTag(MINCRA_MAGICS_KEY, PersistentDataType.TAG_CONTAINER, mincraMagicsContainer);
        };

        final Consumer<Void> setDisplayName = (v) -> {
            if (refineLevel == 0) return; // 精錬されていない場合は何もしない

            final var oraxenId = OraxenItems.getIdByItem(item);
            final var oraxenItem = OraxenItems.getItemById(oraxenId);
            final var baseDisplayName = oraxenItem != null ? oraxenItem.getItemName() :
                    item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() :
                            item.getType().name();
            final var newDisplayName = baseDisplayName + " +" + switch (refineLevel) {
                case 10 -> "E";
                case 11 -> "D";
                case 12 -> "C";
                case 13 -> "B";
                case 14 -> "A";
                case 15 -> "S";
                default -> refineLevel;
            };
            builder.setDisplayName(newDisplayName);
        };

        final Consumer<Void> setLore = (v) -> {
            // "<reset>" と一致する文字列の前までが mainDescriptionLore
            List<String> mainDescriptionLore = descriptionLore.stream()
                    .takeWhile(line -> !line.isEmpty()) // <reset> は空文字に変換される
                    .toList();
            List<String> flavorLore = descriptionLore.stream()
                    .dropWhile(line -> !line.isEmpty())
                    .skip(1) // "<reset>" の次の行から始める
                    .toList();

            List<String> newLore = new ArrayList<>(mainDescriptionLore);

            // Loreの横線
            String divider = Divider.toString(descriptionLore);
            newLore.add(divider);

            // 各スロットに装備されたマテリアルのリスト
            // 表示順に並べ替え
            List<Material> sortedMaterials = materials.stream()
                    .sorted(Comparator.comparingInt(a -> MaterialSlot.getIndexOf(a.slot)))
                    .toList();
            for (final MaterialSlot slot : MaterialSlot.values()) {
                if (availableSlots.isEmpty() || !availableSlots.contains(slot)) {
                    // 使用可能スロットに含まれていない場合はスキップ
                    continue;
                }
                final var material = sortedMaterials.stream()
                        .filter(m -> m.slot.equals(slot.getSlot()))
                        .findFirst();
                String materialName;
                if (material.isPresent()) {
                    String materialId = material.get().id();
                    ItemBuilder materialItemBuilder = OraxenItems.getItemById(materialId);
                    if (materialItemBuilder != null) {
                        materialName = Fonts.material(materialId, false) + Fonts.shift(1) + materialItemBuilder.getItemName();
                    } else {
                        materialName = Fonts.material(materialId, false) + Fonts.shift(1) + materialId;
                    }
                } else {
                    materialName = "-";
                }

                newLore.add(Color.COLOR_WHITE + Fonts.slot(slot, false) + Fonts.shift(2) + materialName);
            }

            newLore.add("<reset>");

            newLore.add(Color.COLOR_WHITE + "装備可能 " + (availableMaterials.isEmpty() ? "<yellow>全て" :
                    availableMaterials.stream()
                            // 末尾の _[0-9]+ を削除
                            .map(id -> id.replaceAll("_[0-9]+$", ""))
                            .distinct()
                            .sorted()
                            .map((id) -> Fonts.material(id + "_1", false))
                            .collect(Collectors.joining(Fonts.shift(1)))
                    ));

            // Loreの横線2
            newLore.add(divider);

            if (!flavorLore.isEmpty()) {
                // フレーバーテキストがある場合は追加
                newLore.addAll(flavorLore);
            }

            builder.setLore(newLore);
        };

        setPDC.accept(null);
        setDisplayName.accept(null);
        setLore.accept(null);
        new RefinementEffect(MincraMagics.getInstance()).apply(builder, item, refineLevel);

        // 既にbuild()したので clearItemStack()しないとTagが付与されない
        builder.clearItemStack();
        return builder;
    }

    @Nullable
    public static ArtifactNBT fromItem(@Nullable ItemStack item) {
        if (item == null) return null;

        final var itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
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
        List<String> loreInNBT = PDCUtils.getStrings(mincramagicsCon, LORE_KEY);

        // Get Available Slots
        List<String> availableSlotsInNBT = PDCUtils.getStrings(mincramagicsCon, AVAILABLE_SLOTS_KEY);
        List<MaterialSlot> availableSlots = availableSlotsInNBT == null ? new ArrayList<>() : availableSlotsInNBT.stream().map(MaterialSlot::fromString)
                .filter(Optional::isPresent).map(Optional::get).toList();
        // Available Materials
        List<String> availableMaterialsInNBT = PDCUtils.getStrings(mincramagicsCon, AVAILABLE_MATERIALS_KEY);
        List<String> availableMaterials = availableMaterialsInNBT == null ? new ArrayList<>() : availableMaterialsInNBT.stream().toList();

        int refineLevel = mincramagicsCon.getOrDefault(REFINE_LEVEL_KEY, PersistentDataType.INTEGER, 0);

        //TODO: Implement MaterialFilters and MagicEnchantments
        return new ArtifactNBT(materials, availableSlots, availableMaterials, loreInNBT, refineLevel);
    }

    /**
     * @return (K, V) = (Slot, Id)
     */
    public Map<MaterialSlot, String> getMaterialMap() {
        return materials.stream()
                .collect(Collectors.toMap(material -> {
                    final var key = MaterialSlot.fromString(material.slot);
                    if (key.isEmpty()) {
                        MincraLogger.warn("Invalid material slot: " + material.slot + ". Defaulting to LEFT slot.");
                        return MaterialSlot.LEFT; // デフォルトはLEFTスロット
                    }
                    return key.get();
                }, material -> material.id));
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

    public ArtifactNBT setRefineLevel(int level) {
        return new ArtifactNBT(
                materials,
                availableSlots,
                availableMaterials,
                descriptionLore,
                level
        );
    }
}
