package jp.mincra.mincramagics.domain.refinement;

import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 精錬値の効果を適用するクラス
 */
public class RefinementEffect {
    private static final Map<ItemType, Map<Attribute, Function<Integer, Double>>> attrEffects = Map.of(
            ItemType.ARMOR, Map.of(
                    Attribute.ARMOR, (level) -> level * 0.01,
                    Attribute.ARMOR_TOUGHNESS, (level) -> level * 0.001
            ),
            ItemType.SWORD, Map.of(
                    Attribute.ATTACK_DAMAGE, (level) -> switch (level) {
                        case 13 -> 0.69;
                        case 14 -> 0.96;
                        case 15 -> 1.25;
                        default -> level * 0.01;
                    }
            )
    );
    private static final Map<ItemType, EquipmentSlotGroup> slotGroups = Map.of(
            ItemType.ARMOR, EquipmentSlotGroup.ARMOR,
            ItemType.SWORD, EquipmentSlotGroup.MAINHAND
    );
    @Deprecated
    private static final NamespacedKey ATTRIBUTE_REFINE_KEY = new NamespacedKey(MincraMagics.getInstance(), "refine");
    @Deprecated
    private static final NamespacedKey ATTRIBUTE_REFINE_RATE_KEY = new NamespacedKey(MincraMagics.getInstance(), "refine_rate");

    private final JavaPlugin plugin;

    public RefinementEffect(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 精錬レベルに応じたエフェクトを追加
     * - 武器の場合: attribute_modifiers.attack_damage +1% * refineLevel (<+13), +169% (+13), +196% (+14), +225% (+15)
     * - 防具の場合: attribute_modifiers.armor +1% * refineLevel, attribute_modifiers.armor_toughness +0.1% * refineLevel
     * @param builder
     * @param refineLevel
     */
    public void apply(ItemBuilder builder, ItemStack item, int refineLevel) {
        if (refineLevel <= 0) return;

        // 初期化 (全消去)
        for (final var attr : AttributeHelper.allAttributes) {
            builder.removeAttributeModifiers(attr, refineEffectKey(attr));
            builder.removeAttributeModifiers(attr, defaultValueKey(attr));
        }

        // デフォルト値を追加
        for (final var entry : AttributeHelper.defaultAttribute(item.getType()).entrySet()) {
            final var attr = entry.getKey();
            final var modifier = entry.getValue();
            builder.addAttributeModifiers(attr, new AttributeModifier(defaultValueKey(attr), modifier.getAmount(), modifier.getOperation(), modifier.getSlotGroup()));
        }

        // refinement 強化の modifier を追加
        final var itemType = ItemType.fromMaterial(item.getType());
        for (final var entry : attrEffects.get(itemType).entrySet()) {
            final var attr = entry.getKey();
            final var amount = entry.getValue().apply(refineLevel);
            builder.addAttributeModifiers(attr, new AttributeModifier(refineEffectKey(attr), amount, AttributeModifier.Operation.ADD_SCALAR, slotGroups.get(itemType)));
        }

        // 古い NamespacedKey の attributeModifier を削除
        builder.removeAttributeModifiers(Attribute.ATTACK_DAMAGE, ATTRIBUTE_REFINE_KEY);
        builder.removeAttributeModifiers(Attribute.ATTACK_DAMAGE, ATTRIBUTE_REFINE_RATE_KEY);
        builder.removeAttributeModifiers(Attribute.ARMOR, ATTRIBUTE_REFINE_KEY);
        builder.removeAttributeModifiers(Attribute.ARMOR, ATTRIBUTE_REFINE_RATE_KEY);
        builder.removeAttributeModifiers(Attribute.ARMOR_TOUGHNESS, ATTRIBUTE_REFINE_KEY);
        builder.removeAttributeModifiers(Attribute.ARMOR_TOUGHNESS, ATTRIBUTE_REFINE_RATE_KEY);

        builder.clearItemStack();
    }

    private NamespacedKey refineEffectKey(Attribute attribute) {
        return new NamespacedKey(plugin, "refine_" + attribute.key().value());
    }

    // アイテムデフォルト値の attributeModifier のためのキー
    private NamespacedKey defaultValueKey(Attribute attribute) {
        return new NamespacedKey(plugin, "default_" + attribute.key().value());
    }
}

enum ItemType {
    ARMOR,
    SWORD;

    public static ItemType fromMaterial(Material material) {
        if (Tag.ITEMS_HEAD_ARMOR.isTagged(material) ||
                Tag.ITEMS_CHEST_ARMOR.isTagged(material) ||
                Tag.ITEMS_LEG_ARMOR.isTagged(material) ||
                Tag.ITEMS_FOOT_ARMOR.isTagged(material)
        ) return ARMOR;

        return SWORD;
    }
}

class AttributeHelper {
    static final List<Attribute> allAttributes = List.of(
            Attribute.MAX_HEALTH,
            Attribute.FOLLOW_RANGE,
            Attribute.KNOCKBACK_RESISTANCE,
            Attribute.MOVEMENT_SPEED,
            Attribute.FLYING_SPEED,
            Attribute.ATTACK_DAMAGE,
            Attribute.ATTACK_KNOCKBACK,
            Attribute.ATTACK_SPEED,
            Attribute.ARMOR,
            Attribute.ARMOR_TOUGHNESS,
            Attribute.FALL_DAMAGE_MULTIPLIER,
            Attribute.LUCK,
            Attribute.MAX_ABSORPTION,
            Attribute.SAFE_FALL_DISTANCE,
            Attribute.SCALE,
            Attribute.STEP_HEIGHT,
            Attribute.GRAVITY,
            Attribute.JUMP_STRENGTH,
            Attribute.BURNING_TIME,
            Attribute.CAMERA_DISTANCE,
            Attribute.EXPLOSION_KNOCKBACK_RESISTANCE,
            Attribute.MOVEMENT_EFFICIENCY,
            Attribute.OXYGEN_BONUS,
            Attribute.WATER_MOVEMENT_EFFICIENCY,
            Attribute.TEMPT_RANGE,
            Attribute.BLOCK_INTERACTION_RANGE,
            Attribute.ENTITY_INTERACTION_RANGE,
            Attribute.BLOCK_BREAK_SPEED,
            Attribute.MINING_EFFICIENCY,
            Attribute.SNEAKING_SPEED,
            Attribute.SUBMERGED_MINING_SPEED,
            Attribute.SWEEPING_DAMAGE_RATIO,
            Attribute.SPAWN_REINFORCEMENTS,
            Attribute.WAYPOINT_TRANSMIT_RANGE,
            Attribute.WAYPOINT_RECEIVE_RANGE
    );

    public static Map<Attribute, AttributeModifier> defaultAttribute(Material material) {
        final var item = new ItemStack(material);
        final var meta = item.getItemMeta();

        if (meta == null) return Map.of();

        return allAttributes.stream().map(attr -> {
                    final var modifiers = meta.getAttributeModifiers(attr);
                    if (modifiers == null) return null;
                    final var first = modifiers.stream().findFirst();
                    return first.map(attributeModifier -> Map.entry(attr, attributeModifier)).orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

    }
}
