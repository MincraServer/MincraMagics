package jp.mincra.mincramagics.nbt.utils;

import jp.mincra.mincramagics.nbt.NamespacedKeys;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * PersistentDataContainer utility.
 */
public class PDCUtils {

    /**
     * Sets a list of strings in the PersistentDataContainer.
     * FIXME: PersistentDataType.LIST.strings() does not work with Oraxen. So we use dataContainers temporarily.
     */
    public static void setStrings(PersistentDataContainer container, NamespacedKey key, List<String> values) {
        if (values == null) {
            container.remove(key);
        } else {
            List<PersistentDataContainer> pdcList = toPDCs(values, container);
            container.set(key, PersistentDataType.LIST.dataContainers(), pdcList);
        }
    }

    /**
     * Retrieves a list of strings from the PersistentDataContainer.
     * FIXME: PersistentDataType.LIST.strings() does not work with Oraxen. So we use dataContainers temporarily.
     */
    public static List<String> getStrings(PersistentDataContainer container, NamespacedKey key) {
        return toStrings(container.get(key, PersistentDataType.LIST.dataContainers()));
    }

    /**
     * Converts a list of strings to a list of PersistentDataContainers.
     */
    private static List<PersistentDataContainer> toPDCs(List<String> values, PersistentDataContainer parent) {
        return values.stream()
                .map(value -> {
                    PersistentDataContainer container = parent.getAdapterContext().newPersistentDataContainer();
                    container.set(NamespacedKeys.VALUE_KEY, PersistentDataType.STRING, value);
                    return container;
                })
                .toList();
    }

    /**
     * Converts a list of PersistentDataContainers to a list of strings.
     * Returns an empty list if the input is null or empty.
     */
    private static List<String> toStrings(@Nullable List<PersistentDataContainer> containers) {
        if (containers == null || containers.isEmpty()) {
            return List.of();
        }
        return containers.stream()
                .map(container -> container.get(NamespacedKeys.VALUE_KEY, PersistentDataType.STRING))
                .filter(Objects::nonNull)
                .toList();
    }
}
