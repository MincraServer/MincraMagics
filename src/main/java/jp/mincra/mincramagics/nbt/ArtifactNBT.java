package jp.mincra.mincramagics.nbt;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MaterialSlot;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.constant.Color;
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

    private static final NamespacedKey ATTRIBUTE_REFINE_KEY = new NamespacedKey(mincra, "refine");
    private static final NamespacedKey ATTRIBUTE_REFINE_RATE_KEY = new NamespacedKey(mincra, "refine_rate");

    private static final Map<org.bukkit.Material, Double> defaultAttackDamage = Map.of(
            org.bukkit.Material.WOODEN_SWORD, 4.0,
            org.bukkit.Material.STONE_SWORD, 5.0,
            org.bukkit.Material.IRON_SWORD, 6.0,
            org.bukkit.Material.GOLDEN_SWORD, 4.0,
            org.bukkit.Material.DIAMOND_SWORD, 7.0,
            org.bukkit.Material.NETHERITE_SWORD, 8.0
    );
    private static final Map<org.bukkit.Material, Double> defaultArmor = Map.ofEntries(
            Map.entry(org.bukkit.Material.LEATHER_HELMET, 1.0),
            Map.entry(org.bukkit.Material.LEATHER_CHESTPLATE, 3.0),
            Map.entry(org.bukkit.Material.LEATHER_LEGGINGS, 2.0),
            Map.entry(org.bukkit.Material.LEATHER_BOOTS, 1.0),
            Map.entry(org.bukkit.Material.CHAINMAIL_HELMET, 2.0),
            Map.entry(org.bukkit.Material.CHAINMAIL_CHESTPLATE, 5.0),
            Map.entry(org.bukkit.Material.CHAINMAIL_LEGGINGS, 4.0),
            Map.entry(org.bukkit.Material.CHAINMAIL_BOOTS, 1.0),
            Map.entry(org.bukkit.Material.IRON_HELMET, 2.0),
            Map.entry(org.bukkit.Material.IRON_CHESTPLATE, 6.0),
            Map.entry(org.bukkit.Material.IRON_LEGGINGS, 5.0),
            Map.entry(org.bukkit.Material.IRON_BOOTS, 2.0),
            Map.entry(org.bukkit.Material.GOLDEN_HELMET, 2.0),
            Map.entry(org.bukkit.Material.GOLDEN_CHESTPLATE, 5.0),
            Map.entry(org.bukkit.Material.GOLDEN_LEGGINGS, 3.0),
            Map.entry(org.bukkit.Material.GOLDEN_BOOTS, 1.0),
            Map.entry(org.bukkit.Material.DIAMOND_HELMET, 3.0),
            Map.entry(org.bukkit.Material.DIAMOND_CHESTPLATE, 8.0),
            Map.entry(org.bukkit.Material.DIAMOND_LEGGINGS, 6.0),
            Map.entry(org.bukkit.Material.DIAMOND_BOOTS, 3.0),
            Map.entry(org.bukkit.Material.NETHERITE_HELMET, 3.0),
            Map.entry(org.bukkit.Material.NETHERITE_CHESTPLATE, 8.0),
            Map.entry(org.bukkit.Material.NETHERITE_LEGGINGS, 6.0),
            Map.entry(org.bukkit.Material.NETHERITE_BOOTS, 3.0)
    );
    private static final Map<org.bukkit.Material, Double> defaultArmorToughness = Map.ofEntries(
            Map.entry(org.bukkit.Material.DIAMOND_HELMET, 2.0),
            Map.entry(org.bukkit.Material.DIAMOND_CHESTPLATE, 2.0),
            Map.entry(org.bukkit.Material.DIAMOND_LEGGINGS, 2.0),
            Map.entry(org.bukkit.Material.DIAMOND_BOOTS, 2.0),
            Map.entry(org.bukkit.Material.NETHERITE_HELMET, 3.0),
            Map.entry(org.bukkit.Material.NETHERITE_CHESTPLATE, 3.0),
            Map.entry(org.bukkit.Material.NETHERITE_LEGGINGS, 3.0),
            Map.entry(org.bukkit.Material.NETHERITE_BOOTS, 3.0)
    );

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

                // availableMaterials
//        newLore.add(Color.COLOR_WHITE + "装着可能マテリアル  " + (availableMaterials.isEmpty() ? "全て" :
//                availableMaterials.stream()
//                        .map(ArtifactNBT::getMaterialGlyph)
//                        .collect(Collectors.joining(SHIFT_2))));
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

        final Consumer<Void> setRefineEffect = (i) -> {
            // 精錬レベルに応じたエフェクトを追加
            // - 武器の場合: attribute_modifiers.attack_damage +1% * refineLevel (<+13), +169% (+13), +196% (+14), +225% (+15)
            // - 防具の場合: attribute_modifiers.armor +1% * refineLevel, attribute_modifiers.armor_toughness +0.1% * refineLevel

            if (refineLevel <= 0) return; // 精錬されていない場合は何もしない

            final var isArmor = Tag.ITEMS_HEAD_ARMOR.isTagged(item.getType()) ||
                    Tag.ITEMS_CHEST_ARMOR.isTagged(item.getType()) ||
                    Tag.ITEMS_LEG_ARMOR.isTagged(item.getType()) ||
                    Tag.ITEMS_FOOT_ARMOR.isTagged(item.getType());

            builder.removeAttributeModifiers(Attribute.ATTACK_DAMAGE, ATTRIBUTE_REFINE_RATE_KEY);
            builder.removeAttributeModifiers(Attribute.ARMOR, ATTRIBUTE_REFINE_RATE_KEY);
            builder.removeAttributeModifiers(Attribute.ARMOR_TOUGHNESS, ATTRIBUTE_REFINE_RATE_KEY);

            if (isArmor) {
                final double armorMultiplier = refineLevel * 0.01;
                final double toughnessMultiplier = refineLevel * 0.001;
                if (builder.getAttributeModifiers(Attribute.ARMOR).isEmpty() && defaultArmor.containsKey(item.getType())) {
                    builder.addAttributeModifiers(Attribute.ARMOR, new AttributeModifier(ATTRIBUTE_REFINE_KEY, defaultArmor.getOrDefault(item.getType(), 1.0), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));
                }
                builder.addAttributeModifiers(Attribute.ARMOR, new AttributeModifier(ATTRIBUTE_REFINE_RATE_KEY, armorMultiplier, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ARMOR));
                if (builder.getAttributeModifiers(Attribute.ARMOR_TOUGHNESS).isEmpty() && defaultArmorToughness.containsKey(item.getType())) {
                    builder.addAttributeModifiers(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(ATTRIBUTE_REFINE_KEY, defaultArmorToughness.getOrDefault(item.getType(), 0.0), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));
                }
                builder.addAttributeModifiers(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(ATTRIBUTE_REFINE_RATE_KEY, toughnessMultiplier, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ARMOR));
            } else {
                final double multiplier = switch (refineLevel) {
                    case 13 -> 0.69;
                    case 14 -> 0.96;
                    case 15 -> 1.25;
                    default -> refineLevel * 0.01;
                };
                if (builder.getAttributeModifiers(Attribute.ATTACK_DAMAGE).isEmpty() && defaultAttackDamage.containsKey(item.getType())) {
                    builder.addAttributeModifiers(Attribute.ATTACK_DAMAGE, new AttributeModifier(ATTRIBUTE_REFINE_KEY, defaultAttackDamage.getOrDefault(item.getType(), 1.0), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
                }
                builder.addAttributeModifiers(Attribute.ATTACK_DAMAGE, new AttributeModifier(ATTRIBUTE_REFINE_RATE_KEY, multiplier, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.MAINHAND));
            }
        };

        setPDC.accept(null);
        setDisplayName.accept(null);
        setLore.accept(null);
        setRefineEffect.accept(null);

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
