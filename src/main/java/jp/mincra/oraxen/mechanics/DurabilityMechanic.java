package jp.mincra.oraxen.mechanics;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import jp.mincra.MincraMagics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

class DurabilityMechanic extends Mechanic {

    static NamespacedKey REAL_DURABILITY_LEFT_KEY = new NamespacedKey(MincraMagics.getInstance(), "Durability");

    private int itemDurability;

    public DurabilityMechanic(MechanicFactory mechanicFactory,
                              ConfigurationSection section) {
        /* We give:
        - an instance of the Factory which created the mechanic
        - the section used to configure the mechanic
        - the item modifier(s)
         */
        super(mechanicFactory, section, item ->
                item.setCustomTag(REAL_DURABILITY_LEFT_KEY,
                        PersistentDataType.INTEGER, section.getInt("value")));
        this.itemDurability = section.getInt("value");
        Bukkit.getLogger().info("[MincraMagics] DurabilityMechanic works right");
    }

    public int getItemMaxDurability() {
        return itemDurability;
    }
}

class DurabilityMechanicFactory extends MechanicFactory {

    public DurabilityMechanicFactory(ConfigurationSection section) {
        super(section);
        MechanicsManager.registerListeners(OraxenPlugin.get(),
                new DurabilityMechanicsManager(this));
        Bukkit.getLogger().info("[MincraMagics] DurabilityMechanicFactory works right");
    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new DurabilityMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        return mechanic;
    }

}

class DurabilityMechanicsManager implements Listener {

    private DurabilityMechanicFactory factory;

    public DurabilityMechanicsManager(DurabilityMechanicFactory factory) {
        this.factory = factory;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onItemDamaged(PlayerItemDamageEvent event) {
        Bukkit.getLogger().info("[MincraMagics] DurabilityMechanicsManager works right");

        ItemStack item = event.getItem();
        String itemID = OraxenItems.getIdByItem(item);
        if (factory.isNotImplementedIn(itemID))
            return;

        DurabilityMechanic durabilityMechanic =
                (DurabilityMechanic) factory.getMechanic(itemID);

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer persistentDataContainer =
                itemMeta.getPersistentDataContainer();
        int realDurabilityLeft = persistentDataContainer
                .get(DurabilityMechanic.REAL_DURABILITY_LEFT_KEY, PersistentDataType.INTEGER)
                - event.getDamage();

        if (realDurabilityLeft > 0) {
            double realMaxDurability =
                    //because int rounded values suck
                    durabilityMechanic.getItemMaxDurability();
            persistentDataContainer.set(DurabilityMechanic.REAL_DURABILITY_LEFT_KEY,
                    PersistentDataType.INTEGER, realDurabilityLeft);
            ((Damageable) itemMeta).setDamage((int) (item.getType()
                    .getMaxDurability() - realDurabilityLeft
                    / realMaxDurability * item.getType().getMaxDurability()));
            item.setItemMeta(itemMeta);
        } else {
            item.setAmount(0);
        }

    }

}