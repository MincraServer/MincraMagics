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
    LEFT("left", TriggerType.LEFT),
    RIGHT("right", TriggerType.RIGHT),
    SWAP("swap", TriggerType.SWAP),
    DROP("drop", TriggerType.DROP),
    PASSIVE1("passive_1", TriggerType.PASSIVE),
    PASSIVE2("passive_2", TriggerType.PASSIVE),;

    private final String slot;
    private final TriggerType triggerType;

    MaterialSlot(String slot, TriggerType triggerType) {
        this.slot = slot;
        this.triggerType = triggerType;
    }

    public String getSlot() {
        return slot;
    }

    public TriggerType getTriggerType() {
        return triggerType;
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
