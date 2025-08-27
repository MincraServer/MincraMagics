package jp.mincra.mincramagics.gui;

import jp.mincra.mincramagics.gui.impl.JobRewardMenu;
import jp.mincra.mincramagics.gui.impl.MaterialEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<String> getRegisteredGuiIds() {
        return new ArrayList<>(idToGui.keySet());
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
        registerGui("MaterialEditor2", MaterialEditor.class);
        registerGui("JobRewardMenu", JobRewardMenu.class);
    }

    public boolean open(String guiId, Player target) {
        InventoryGUI inventoryGUI = instantiateGui(guiId);
        if (inventoryGUI == null) {
            return false;
        } else {
            pluginManager.registerEvents(inventoryGUI, plugin);
            inventoryGUI.open(target);
            return true;
        }
    }
}
