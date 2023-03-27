package jp.mincra.mincramagics.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public abstract class InventoryGUI implements Listener {
    public InventoryGUI() {}

    protected abstract void open(Player player);
    public abstract Inventory getInventory();
}
