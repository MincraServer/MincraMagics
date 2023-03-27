package jp.mincra.mincramagics.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GUIManager implements Listener {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
//    private final Map<UUID, InventoryGUI> openedGui;

    public GUIManager(JavaPlugin plugin) {
        this.plugin = plugin;
        pluginManager = Bukkit.getPluginManager();
//        openedGui = new HashMap<>();
    }

    public void open(InventoryGUI gui, Player target) {
        pluginManager.registerEvents(gui, plugin);
//        openedGui.put(target.getUniqueId(), gui);
        gui.open(target);
    }

//    @EventHandler
//    private void onClose(InventoryCloseEvent e) {
//        Inventory closedInv = e.getInventory();
//        for (InventoryGUI gui : openedGui.values()) {
//            if (gui.getInventory().equals(closedInv)) {
//                gui.onClose();
//                pluginManager.even
//            }
//        }
//    }
}
