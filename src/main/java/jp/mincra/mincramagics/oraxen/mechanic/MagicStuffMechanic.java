package jp.mincra.mincramagics.oraxen.mechanic;

import com.google.gson.Gson;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.nbtobject.Material;
import jp.mincra.mincramagics.nbtobject.MincraNBT;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Function;

public class MagicStuffMechanic extends Mechanic {
    private static final Set<String> availableSlot = Set.of("left", "right", "drop", "swap");

    protected MagicStuffMechanic(MechanicFactory mechanicFactory, ConfigurationSection section, Function<ItemBuilder, ItemBuilder>... modifiers) {
        super(mechanicFactory, section, itemBuilder -> {
            MincraNBT mincraNbt = new MincraNBT();

            // Configure Materials
            ConfigurationSection materialSec = section.getConfigurationSection("material");
            if (materialSec != null) {
                Set<String> slotKeys = materialSec.getKeys(false);

                for (String key : slotKeys) {
                    if (availableSlot.contains(key)) {
                        String materialId = materialSec.getString(key);
                        mincraNbt.materials.add(new Material(key, materialId));
                    }
                }
            }

            //TODO: Implement MaterialFilters and MagicEnchantments

            NBTItem nbtI = new NBTItem(itemBuilder.build());
            System.out.println("[MincraMagics] Loaded item");
            System.out.println(new Gson().toJson(mincraNbt));
            NBTContainer mincraNbtCom = new NBTContainer(new Gson().toJson(mincraNbt));
            nbtI.mergeCompound(mincraNbtCom);
            ItemStack item = nbtI.getItem();
            return new ItemBuilder(item);
        });
    }
}
