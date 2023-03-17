package jp.mincra.mincramagics.oraxen.mechanic;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;

public class DurabilityMechanic extends Mechanic {

    static NamespacedKey REAL_DURABILITY_LEFT_KEY = new NamespacedKey(MincraMagics.getInstance(), "Durability");

    private int itemDurability;

    public DurabilityMechanic(MechanicFactory mechanicFactory,
                              ConfigurationSection section) {
        /* We give:
        - an instance of the Factory which created the mechanic
        - the section used to configure the mechanic
        - the item modifier(s)
         */
        super(mechanicFactory, section, item ->
                item.setCustomTag(REAL_DURABILITY_LEFT_KEY,
                        PersistentDataType.INTEGER, section.getInt("value")));
        this.itemDurability = section.getInt("value");
        System.out.println("[MincraMagics] DurabilityMechanic works right?");
    }

    public int getItemMaxDurability() {
        return itemDurability;
    }
}

