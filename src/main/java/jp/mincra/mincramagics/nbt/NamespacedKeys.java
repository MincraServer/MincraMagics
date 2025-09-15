package jp.mincra.mincramagics.nbt;

import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class NamespacedKeys {
    private static final JavaPlugin mincra = MincraMagics.getInstance();

    // common
    public static final NamespacedKey VALUE_KEY = new NamespacedKey(mincra, "value");
}
