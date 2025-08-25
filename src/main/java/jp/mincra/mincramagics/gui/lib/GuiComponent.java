package jp.mincra.mincramagics.gui.lib;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class GuiComponent {
    private final Map<Integer, List<Consumer<InventoryClickEvent>>> clickListeners = new HashMap<>();

    public abstract void render(Inventory inv);

    protected void addClickListener(int index, Consumer<InventoryClickEvent> listener) {
        if (!clickListeners.containsKey(index)) {
            clickListeners.put(index, new ArrayList<>());
        }
        clickListeners.get(index).add(listener);
    }

    public Map<Integer, List<Consumer<InventoryClickEvent>>> getClickListeners() {
        return clickListeners;
    }
}
