//package jp.mincra.mincramagics.mechanics;
//
//import io.th0rgal.oraxen.OraxenPlugin;
//import io.th0rgal.oraxen.mechanics.Mechanic;
//import io.th0rgal.oraxen.mechanics.MechanicFactory;
//import io.th0rgal.oraxen.mechanics.MechanicsManager;
//import org.bukkit.configuration.ConfigurationSection;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class RecipeMechanicFactory extends MechanicFactory {
//
//    private final List<RecipeMechanic> mechanics = new ArrayList<>();
//
//    public RecipeMechanicFactory(ConfigurationSection section) {
//        super(section);
//        MechanicsManager.registerListeners(OraxenPlugin.get(),
//                new RecipeMechanicsListener(this));
//    }
//
//    @Override
//    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
//        RecipeMechanic mechanic = new RecipeMechanic(this, itemMechanicConfiguration);
//        addToImplemented(mechanic);
//        mechanics.add(mechanic);
//        return mechanic;
//    }
//
//    public List<RecipeMechanic> getMechanics() {
//        return mechanics;
//    }
//}
