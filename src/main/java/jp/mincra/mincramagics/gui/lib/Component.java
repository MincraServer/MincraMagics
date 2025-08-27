package jp.mincra.mincramagics.gui.lib;

import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Component {
    private final Map<Integer, List<Consumer<InventoryClickEvent>>> clickListeners = new HashMap<>();
    private final Map<Integer, List<Consumer<InventoryDragEvent>>> dragListeners = new HashMap<>();
    private final List<Consumer<InventoryCloseEvent>> closeListeners = new ArrayList<>();

    public abstract void render(Inventory inv);

    protected record ItemEvent<T extends Event>(ItemStack item, T event) {}

    // Primitive listener adders

    protected void addClickListener(int index, Consumer<InventoryClickEvent> listener) {
        if (index <= -1) {
            MincraLogger.warn("Do not use negative index except -1 (any slot). Use addClickListener(Consumer<InventoryClickEvent>) instead.");
        }
        if (!clickListeners.containsKey(index)) {
            clickListeners.put(index, new ArrayList<>());
        }
        clickListeners.get(index).add(listener);
    }

    protected void addClickListener(Consumer<InventoryClickEvent> listener) {
        // -1 is the index for "any slot"
        addClickListener(-1, listener);
    }

    protected void addDragListener(int index, Consumer<InventoryDragEvent> listener) {
        if (!dragListeners.containsKey(index)) {
            dragListeners.put(index, new ArrayList<>());
        }
        dragListeners.get(index).add(listener);
    }

    protected void addCloseListener(int index, Consumer<InventoryCloseEvent> listener) {
        closeListeners.add(listener);
    }

    // Specialized listener adders

    protected void addMoveToOtherInventoryListener(int index, Consumer<ItemEvent<InventoryClickEvent>> listener) {
        addClickListener(index, e -> {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                listener.accept(new ItemEvent<>(e.getCurrentItem(), e));
            }
        });
    }

    protected void addPlaceListener(int index, Consumer<ItemEvent<InventoryClickEvent>> listener) {
        addClickListener(index, e -> {
            if (List.of(
                    InventoryAction.PLACE_ALL, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ONE,
                    InventoryAction.PLACE_FROM_BUNDLE, InventoryAction.PLACE_ALL_INTO_BUNDLE, InventoryAction.PLACE_SOME_INTO_BUNDLE
            ).contains(e.getAction())) {
                if (e.getRawSlot() != index) return;
                listener.accept(new ItemEvent<>(e.getCursor(), e));
            }
        });
    }

    protected void addPickupListener(int index, Consumer<ItemEvent<InventoryClickEvent>> listener) {
        addClickListener(index, e -> {
            if (List.of(
                    InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE,
                    InventoryAction.PICKUP_SOME, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_SLOT
            ).contains(e.getAction())) {
                if (e.getRawSlot() != index) return;
                listener.accept(new ItemEvent<>(e.getCurrentItem(), e));
            }
        });
    }

    protected void addSwapListener(int index, Consumer<ItemEvent<InventoryClickEvent>> listener) {
        addClickListener(index, e -> {
            if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                if (e.getRawSlot() != index) return;
                listener.accept(new ItemEvent<>(e.getCurrentItem(), e));
            }
        });
    }

    // Getters

    public Map<Integer, List<Consumer<InventoryClickEvent>>> getClickListeners() {
        return clickListeners;
    }

    public Map<Integer, List<Consumer<InventoryDragEvent>>> getDragListeners() {
        return dragListeners;
    }

    public List<Consumer<InventoryCloseEvent>> getCloseListeners() {
        return closeListeners;
    }
}
