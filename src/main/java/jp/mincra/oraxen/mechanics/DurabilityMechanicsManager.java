package jp.mincra.oraxen.mechanics;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DurabilityMechanicsManager implements Listener {

    private DurabilityMechanicFactory factory;

    public DurabilityMechanicsManager(DurabilityMechanicFactory factory) {
        this.factory = factory;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onItemDamaged(PlayerItemDamageEvent event) {
        Bukkit.getLogger().info("[MincraMagics] DurabilityMechanicsManager works right?");

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
