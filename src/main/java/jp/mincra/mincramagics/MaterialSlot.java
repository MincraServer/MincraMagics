package jp.mincra.mincramagics;

import jp.mincra.mincramagics.oraxen.mechanic.artifact.TriggerType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

/**
 * MaterialSlot represents the slots in which a material can be used.
 * Note that this is different from TriggerType, which is used for skill activation.
 * For instance, MaterialSlot may contain a passive slot 1, a passive slot 2, and a passive slot 3,
 * but TriggerType contains only PASSIVE as a trigger type.
 */
public enum MaterialSlot {
    LEFT("left", TriggerType.LEFT, 1),
    RIGHT("right", TriggerType.RIGHT, 2),
    SWAP("swap", TriggerType.SWAP, 3),
    DROP("drop", TriggerType.DROP, 4),
    PASSIVE1("passive_1", TriggerType.PASSIVE, 5),
    PASSIVE2("passive_2", TriggerType.PASSIVE, 6),;

    private final String slot;
    private final TriggerType triggerType;
    private final int order;

    MaterialSlot(String slot, TriggerType triggerType, int order) {
        this.slot = slot;
        this.triggerType = triggerType;
        this.order = order;
    }

    public String getSlot() {
        return slot;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public int getOrder() {
        return order;
    }

    public static Optional<MaterialSlot> fromString(String slot) {
        return Arrays.stream(values())
                .filter(materialSlot -> materialSlot.slot.equalsIgnoreCase(slot))
                .findFirst();
    }

    public static int indexOf(MaterialSlot slot) {
        return Arrays.asList(values()).indexOf(slot);
    }

    @Nullable
    public static MaterialSlot fromIndex(int index) {
        if (index < 0 || index >= values().length) {
            return null;
        }
        return values()[index];
    }

    @Nullable
    public static String fromIndexAsString(int index) {
        MaterialSlot slot = fromIndex(index);
        return slot != null ? slot.getSlot() : null;
    }

    public static int getIndexOf(String slot) {
        return Arrays.stream(values())
                .filter(materialSlot -> materialSlot.slot.equalsIgnoreCase(slot))
                .findFirst()
                .map(MaterialSlot::ordinal)
                .orElse(-1);
    }
}
