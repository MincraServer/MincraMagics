package jp.mincra.mincramagics.oraxen.mechanic.broom;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import jp.mincra.mincramagics.MincraMagics;
import org.bukkit.configuration.ConfigurationSection;

public class BroomMechanicFactory extends MechanicFactory {

    public BroomMechanicFactory(String mechanicId) {
        super(mechanicId);
        final var manager = new BroomMechanicManager(this, MincraMagics.getInstance());
        MechanicsManager.registerListeners(OraxenPlugin.get(), mechanicId, manager);
        PacketEvents.getAPI().getEventManager().registerListener(manager, PacketListenerPriority.NORMAL);
    }

    @Override
    public Mechanic parse(ConfigurationSection section) {
        Mechanic mechanic = new BroomMechanic(this, section);
        addToImplemented(mechanic);
        return mechanic;
    }
}