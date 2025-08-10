package jp.mincra.mincramagics.nbtobject;

import java.util.Arrays;
import java.util.Optional;

/**
 * MaterialSlot represents the slots in which a material can be used.
 * Note that this is different from TriggerType, which is used for skill activation.
 */
public enum MaterialSlot {
    LEFT("left"),
    RIGHT("right"),
    SWAP("swap"),
    DROP("drop");

    private final String slot;

    MaterialSlot(String slot) {
        this.slot = slot;
    }

    public String getSlot() {
        return slot;
    }

    public static Optional<MaterialSlot> fromString(String slot) {
        return Arrays.stream(values())
                .filter(materialSlot -> materialSlot.slot.equalsIgnoreCase(slot))
                .findFirst();
    }
}
