package jp.mincra.mincramagics.oraxen.mechanic.material;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.configuration.ConfigurationSection;

public class MaterialMechanic extends Mechanic {
    public MaterialMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section, item -> item);
        String materialId = section.getString("materialId");
        String skillId = section.getString("skillId");
        double cooldown = section.getDouble("cooldown");
        double mp = section.getDouble("mp");

        MaterialProperty materialProperty = new MaterialProperty(materialId, skillId, (float) mp, (float) cooldown);
        MincraMagics.getMaterialManager().registerMaterial(materialId, materialProperty);
    }
}

