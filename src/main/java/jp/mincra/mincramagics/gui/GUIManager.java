package jp.mincra.mincramagics.gui;

import jp.mincra.mincramagics.gui.impl.MaterialEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GUIManager implements Listener {
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final Map<String, Class<? extends InventoryGUI>> idToGui;
//    private final Map<UUID, InventoryGUI> openedGui;

    public GUIManager(JavaPlugin plugin) {
        this.plugin = plugin;
        pluginManager = Bukkit.getPluginManager();
        idToGui = new HashMap<>();
        registerDefault();
//        openedGui = new HashMap<>();
    }

    public void registerGui(String id, Class<? extends InventoryGUI> clazz) {
        idToGui.put(id, clazz);
    }

    @Nullable
    private InventoryGUI instantiateGui(String id) {
        if (!idToGui.containsKey(id)) {
            return null;
        }

        try {
            return idToGui.get(id).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerDefault() {
        registerGui("MaterialEditor", MaterialEditor.class);
    }

    public void open(InventoryGUI gui, Player target) {
        pluginManager.registerEvents(gui, plugin);
//        openedGui.put(target.getUniqueId(), gui);
        gui.open(target);
    }

    public boolean open(String guiId, Player target) {
        InventoryGUI inventoryGUI = instantiateGui(guiId);
        if (inventoryGUI == null) {
            return false;
        } else {
            open(inventoryGUI, target);
            return true;
        }
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
