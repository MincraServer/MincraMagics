package jp.mincra.mincramagics.nbt;

import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class NamespacedKeys {
    private static final JavaPlugin mincra = MincraMagics.getInstance();

    public static final NamespacedKey MINCRA_MAGICS_KEY = new NamespacedKey(mincra, "MincraMagics");

    // Material
    public static final NamespacedKey MATERIALS_KEY = new NamespacedKey(mincra, "Materials");
    public static final NamespacedKey SLOT_KEY = new NamespacedKey(mincra, "Slot");
    public static final NamespacedKey ID_KEY = new NamespacedKey(mincra, "Id");
    public static final NamespacedKey LORE_KEY = new NamespacedKey(mincra, "LORE");
    public static final NamespacedKey AVAILABLE_SLOTS_KEY = new NamespacedKey(mincra, "AvailableSlots");
    public static final NamespacedKey AVAILABLE_MATERIALS_KEY = new NamespacedKey(mincra, "AvailableMaterials");

    // common
    public static final NamespacedKey VALUE_KEY = new NamespacedKey(mincra, "value");
}
