package jp.mincra.mincramagics.gui;

import jp.mincra.mincramagics.gui.screen.JobRewardListMenu;
import jp.mincra.mincramagics.gui.screen.JobRewardMenu;
import jp.mincra.mincramagics.gui.screen.MaterialEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIManager implements Listener {
    private final Map<String, Class<? extends GUI>> idToGui;

    public GUIManager() {
        idToGui = new HashMap<>();
        registerDefault();
    }

    public void registerGui(String id, Class<? extends GUI> clazz) {
        idToGui.put(id, clazz);
    }

    public List<String> getRegisteredGuiIds() {
        return new ArrayList<>(idToGui.keySet());
    }

    @Nullable
    private GUI instantiateGui(String id) {
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
        registerGui("JobRewardMenu", JobRewardMenu.class);
        registerGui("JobRewardListMenu", JobRewardListMenu.class);
    }

    public boolean open(String guiId, Player target) {
        GUI gui = instantiateGui(guiId);
        if (gui == null) {
            return false;
        } else {
            gui.open(target);
            return true;
        }
    }
}
