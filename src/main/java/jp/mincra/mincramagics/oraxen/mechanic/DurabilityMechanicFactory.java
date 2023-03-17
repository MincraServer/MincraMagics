package jp.mincra.mincramagics.oraxen.mechanic;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.configuration.ConfigurationSection;

public class DurabilityMechanicFactory extends MechanicFactory {

    public DurabilityMechanicFactory(ConfigurationSection section) {
        super(section);
        MechanicsManager.registerListeners(OraxenPlugin.get(),
                new DurabilityMechanicsManager(this));
        System.out.println("[MincraMagics] DurabilityMechanicFactory works right?");
    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new DurabilityMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        return mechanic;
    }
}
