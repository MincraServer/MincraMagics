package jp.mincra.oraxen.mechanics;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import jp.mincra.MincraMagics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class MagicStuffMechanicFactory extends MechanicFactory {
    public MagicStuffMechanicFactory(ConfigurationSection section) {
        super(section);
        MechanicsManager.registerListeners(OraxenPlugin.get(),
                new MagicStuffMechanicManager(this));
    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new MagicStuffMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        Bukkit.getLogger().info("MagicStuffMechanicFactory#.parse()");
        return mechanic;
    }
}
