package jp.mincra.mincramagics.oraxen.mechanic;

import de.tr7zw.nbtapi.NBTItem;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MagicStuffMechanicManager implements Listener {
    private final MagicStuffMechanicFactory factory;

    public MagicStuffMechanicManager(MagicStuffMechanicFactory factory) {
        this.factory = factory;
    }

    @EventHandler
    private void onClick(PlayerInteractEvent e) {
        System.out.println("MagicStuffMechanicManager#.onClick() run");
        Player player = e.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack item = inv.getItemInMainHand();
        String itemId = OraxenItems.getIdByItem(item);
        if (factory.isNotImplementedIn(itemId))
            return;
        System.out.println("MagicStuffMechanicManager#.onClick() is implemented");

        MagicStuffMechanic magicStuffMec = (MagicStuffMechanic) factory.getMechanic(itemId);

        NBTItem nbtItem = new NBTItem(item);
    }
}
