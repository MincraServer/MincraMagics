package jp.mincra.oraxen.mechanics;

import de.tr7zw.nbtapi.NBTItem;
import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.nbt.object.MincraNBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MagicStuffMechanicManager implements Listener {
    private MagicStuffMechanicFactory factory;

    public MagicStuffMechanicManager(MagicStuffMechanicFactory factory) {
        this.factory = factory;
    }

    @EventHandler
    private void onClick(PlayerInteractEvent e) {
        Bukkit.getLogger().info("MagicStuffMechanicManager#.onClick() run");
        Player player = e.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack item = inv.getItemInMainHand();
        String itemId = OraxenItems.getIdByItem(item);
        if (factory.isNotImplementedIn(itemId))
            return;
        Bukkit.getLogger().info("MagicStuffMechanicManager#.onClick() is implemented");

        MagicStuffMechanic magicStuffMec = (MagicStuffMechanic) factory.getMechanic(itemId);

        NBTItem nbtItem = new NBTItem(item);
    }
}
