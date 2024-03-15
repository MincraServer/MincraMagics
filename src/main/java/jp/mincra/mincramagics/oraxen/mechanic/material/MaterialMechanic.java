package jp.mincra.mincramagics.oraxen.mechanic.material;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbtobject.MaterialNBT;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.configuration.ConfigurationSection;

public class MaterialMechanic extends Mechanic {
    protected MaterialMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section, item -> {
            MaterialNBT materialNBT = new MaterialNBT(section.getDouble("cooldown"), section.getDouble("mp"));
            return materialNBT.setNBTTag(item);
        });
        String materialId = section.getString("materialId");
        String skillId = section.getString("skillId");
        double cooldown = section.getDouble("cooldown");
        double mp = section.getDouble("mp");
        double strength = section.getDouble("strength");

        MaterialProperty materialProperty = new MaterialProperty(materialId, skillId, (float) cooldown, (float) mp, (float) strength);
        MincraMagics.getMaterialManager().registerMaterial(materialId, materialProperty);
    }
}

