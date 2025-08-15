package jp.mincra.mincramagics.oraxen.mechanic.material;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbtobject.MaterialNBT;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialMechanic extends Mechanic {
    protected MaterialMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section, item -> {
            MaterialNBT materialNBT = new MaterialNBT(section.getDouble("cooldown"), section.getDouble("mp"));
            return materialNBT.setNBTTag(item);
        });

        final Logger logger = MincraMagics.getPluginLogger();

        String materialId = section.getString("materialId");
        String skillId = section.getString("skillId");
        double cooldown = section.getDouble("cooldown");
        double mp = section.getDouble("mp");
        // Fallback to strength if level is not set
        if (!section.contains("level") && section.contains("strength")) {
            logger.log(Level.WARNING, "Material " + materialId + " uses deprecated property 'strength'. Please use 'level' instead.");
        }
        double level = section.getDouble("level", section.getDouble("strength", 1.0));

        boolean hasExtra = section.isConfigurationSection("extra");
        if (!hasExtra) {
            logger.log(Level.WARNING, "'extra' section must be a map for material " + materialId + ". Using empty map as default.");
        }
        ConfigurationSection extraSection = section.getConfigurationSection("extra");
        Map<String, Object> extra = extraSection != null ? extraSection.getValues(false) : Map.of();

        MaterialProperty materialProperty = new MaterialProperty(materialId, skillId, (float) cooldown, (float) mp, (float) level, extra);
        MincraMagics.getMaterialManager().registerMaterial(materialId, materialProperty);
    }
}

