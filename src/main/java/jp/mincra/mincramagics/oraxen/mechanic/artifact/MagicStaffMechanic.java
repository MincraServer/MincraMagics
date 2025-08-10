package jp.mincra.mincramagics.oraxen.mechanic.artifact;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbtobject.Material;
import jp.mincra.mincramagics.nbtobject.MagicStaffNBT;
import jp.mincra.mincramagics.MaterialSlot;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Logger;
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

            final Logger logger = MincraMagics.getPluginLogger();

            // get 'available_slots' section, which is an array of string
            List<MaterialSlot> availableSlots = section.getStringList("available_slots").stream().map(MaterialSlot::fromString).filter((slot) -> {
                if (slot.isEmpty()) {
                    logger.warning("Invalid slot in 'available_slots': " + slot);
                }
                return slot.isPresent();
            }).map(Optional::get).toList();

            // get 'available_materials' section, which is an array of string
            List<String> availableMaterials = section.getStringList("available_materials");

            //TODO: Implement MaterialFilters and MagicEnchantments
            MagicStaffNBT magicStaffNBT = new MagicStaffNBT(materials, availableSlots, availableMaterials, itemBuilder.getLore());

            return magicStaffNBT.setNBTTag(itemBuilder);
        });
    }
}
