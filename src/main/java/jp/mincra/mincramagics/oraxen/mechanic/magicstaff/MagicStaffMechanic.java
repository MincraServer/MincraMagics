package jp.mincra.mincramagics.oraxen.mechanic.magicstaff;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.nbtobject.Material;
import jp.mincra.mincramagics.nbtobject.MagicStaffNBT;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MagicStaffMechanic extends Mechanic {
    private static final Set<String> availableSlot = Arrays.stream(TriggerType.values())
            .map(triggerType -> triggerType.toString().toLowerCase()).collect(Collectors.toSet());

    protected MagicStaffMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section, itemBuilder -> {
            List<Material> materials = new ArrayList<>();

            // Configure Materials
            ConfigurationSection materialSec = section.getConfigurationSection("material");
            if (materialSec != null) {
                Set<String> slotKeys = materialSec.getKeys(false);

                for (String key : slotKeys) {
                    if (availableSlot.contains(key)) {
                        String materialId = materialSec.getString(key);
                        materials.add(new Material(key, materialId));
                    }
                }
            }

            //TODO: Implement MaterialFilters and MagicEnchantments
            MagicStaffNBT magicStaffNBT = new MagicStaffNBT(
                    materials,
                    null,
                    null,
                    itemBuilder.getLore());

            return magicStaffNBT.setNBTTag(itemBuilder);
        });
    }
}
