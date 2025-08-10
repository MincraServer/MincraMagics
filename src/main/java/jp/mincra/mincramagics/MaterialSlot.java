package jp.mincra.mincramagics;

import javax.annotation.Nullable;
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
