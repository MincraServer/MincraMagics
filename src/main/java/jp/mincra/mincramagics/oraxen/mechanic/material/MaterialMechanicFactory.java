package jp.mincra.mincramagics.oraxen.mechanic.material;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.configuration.ConfigurationSection;

public class MaterialMechanicFactory extends MechanicFactory {

    public MaterialMechanicFactory(String mechanicId) {
        super(mechanicId);
        MechanicsManager.registerListeners(OraxenPlugin.get(), mechanicId,
                new MaterialMechanicsManager(this));
    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new MaterialMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        return mechanic;
    }

}
