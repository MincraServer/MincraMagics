package jp.mincra.mincramagics.gui;

import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.gui.lib.Screen;
import jp.mincra.mincramagics.gui.lib.GUIHelper;
import jp.mincra.mincramagics.gui.lib.Component;
import jp.mincra.mincramagics.gui.lib.State;
import jp.mincra.mincramagics.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class GUI implements Listener {
    private Player player;

    private final Map<Integer, List<Consumer<InventoryClickEvent>>> clickListeners = new HashMap<>();
    private final Map<Integer, List<Consumer<InventoryDragEvent>>> dragListeners = new HashMap<>();
    private final List<Consumer<InventoryCloseEvent>> closeListeners = new ArrayList<>();

    private final List<Object> states = new ArrayList<>();
    private final UpdateQueue updateQueue = new UpdateQueue();
    private Inventory inv;
    private Screen prevScreen;
    private int lastStateIndex = -1;
    private boolean lockRerendering = false;
    private Predicate<Integer> isModifiableSlot = (index) -> false;

    // API
    public final void open(Player player) {
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, MincraMagics.getInstance());
        render();
    }

    // Rendering
    @Nullable
    protected abstract Screen build(BuildContext context);

    protected final void addClickListener(int index, Consumer<InventoryClickEvent> listener) {
        if (!clickListeners.containsKey(index)) {
            clickListeners.put(index, new ArrayList<>());
        }
        clickListeners.get(index).add(listener);
    }

    ///  Add global click listener
    protected final void addClickListener(Consumer<InventoryClickEvent> listener) {
        // -1 is the index for "any slot"
        addClickListener(-1, listener);
    }

    protected final void addDragListener(Integer index, Consumer<InventoryDragEvent> listener) {
        if (!dragListeners.containsKey(index)) {
            dragListeners.put(index, new ArrayList<>());
        }
        dragListeners.get(index).add(listener);
    }

    protected final void addDragListener(Stream<Integer> indexes, Consumer<InventoryDragEvent> listener) {
        indexes.forEach(index -> addDragListener(index, listener));
    }

    protected final void addCloseListener(Consumer<InventoryCloseEvent> listener) {
        closeListeners.add(listener);
    }

    protected final void addCloseListeners(List<Consumer<InventoryCloseEvent>> listeners) {
        closeListeners.addAll(listeners);
    }

    protected final <T> State<T> useState(@NotNull T initialValue) {
        final int thisIndex = ++lastStateIndex;
        final T currentState = getStateOrDefault(thisIndex, initialValue);
        MincraLogger.debug("[useState] lastStateIndex: " + thisIndex + ", states: " + Strings.truncate(states) + ", currentState: " + Strings.truncate(currentState));
        if (thisIndex < states.size()) {
            states.set(thisIndex, currentState);
        } else {
            states.add(currentState);
        }

        return new State<>(currentState, (setter) -> {
            final T newState = getStateOrDefault(thisIndex, initialValue);
            final T updatedState = setter.apply(newState);
//            MincraLogger.debug("[State] states before rerender: " + states);
            if (lockRerendering) {
                updateQueue.add(thisIndex, updatedState);
                return null;
            }
//            MincraLogger.debug("[State] Updating state at index " + thisIndex + " to " + updatedState);
            states.set(thisIndex, updatedState);
            // Rerender the GUI
            render();
//            MincraLogger.debug("[State] states after rerender: " + states);
            return null;
        });
    }

    private <T> T getStateOrDefault(int index, T defaultValue) {
        final Object thisState = index < states.size() ? states.get(index) : null;
        final boolean isInstance = defaultValue != null && defaultValue.getClass().isInstance(thisState);
        if (thisState != null && defaultValue != null && !isInstance) {
            MincraLogger.warn(String.format("State at index %d is not of type %s, but %s. Using default value instead.",
                    index, defaultValue.getClass().getName(), thisState.getClass().getName()));
        }

        @SuppressWarnings("unchecked") // We assume the type is correct
        final T currentState = isInstance ? (T) thisState : defaultValue;

        return currentState;
    }

    private void render() {
        // FIXME: clear() よりインデックス書き換えの方がパフォーマンスが良いかもしれない
        lastStateIndex = -1;
        lockRerendering = true;
        clickListeners.clear();
        dragListeners.clear();
        closeListeners.clear();

        final Screen screen = build(BuildContext.builder()
                .player(player)
                .isFirstRender(prevScreen == null)
                .build());
        if (screen == null) return;

        isModifiableSlot = screen.isModifiableSlot();
        if (screen.shouldReopen(prevScreen)) {
            MincraLogger.debug("Opening inventory for the first time.");
            inv = Bukkit.createInventory(null, screen.size(), net.kyori.adventure.text.Component.text(screen.title()));
            player.openInventory(inv);
        }

        if (!screen.equals(prevScreen)) {
            GUIHelper.updateTitle(player, screen.title());
        }

        for (Component component : screen.components()) {
            component.render(inv);

            for (Map.Entry<Integer, List<Consumer<InventoryClickEvent>>> entry : component.getClickListeners().entrySet()) {
                int index = entry.getKey();
                List<Consumer<InventoryClickEvent>> listeners = entry.getValue();
                for (Consumer<InventoryClickEvent> listener : listeners) {
                    addClickListener(index, listener);
                }
            }

            for (Map.Entry<Integer, List<Consumer<InventoryDragEvent>>> entry : component.getDragListeners().entrySet()) {
                int index = entry.getKey();
                List<Consumer<InventoryDragEvent>> listeners = entry.getValue();
                for (Consumer<InventoryDragEvent> listener : listeners) {
                    addDragListener(index, listener);
                }
            }

            addCloseListeners(component.getCloseListeners());
        }

        lockRerendering = false;
        prevScreen = screen;
        updateQueue.apply(item -> {
            int index = item.index();
            Object newValue = item.newValue();
            if (index >= lastStateIndex) {
                MincraLogger.warn(String.format("State index %d is out of bounds (last index: %d).", index, lastStateIndex));
                return null; // Skip if the index is not valid
            }
            MincraLogger.debug("InventoryGUI: old states: " + states);
            states.set(index, newValue);
            MincraLogger.debug("InventoryGUI: new states: " + states);
            // Rerender
            render();
            return null;
        });
    }

    // Event handlers
    @EventHandler // TODO: Make this method final
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;

        final var destinationSlot = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                ? GUIHelper.calculateDestinationSlot(event)
                : event.getRawSlot();
        MincraLogger.debug("[onClick] rawSlot: " + event.getRawSlot() + ", slot: " + event.getSlot() + ", action: " + event.getAction() + ", destination slot: " + destinationSlot);

        event.setCancelled(!isModifiableSlot.test(event.getRawSlot()));

//        MincraLogger.debug("[onClick] states: " + states);
        if (clickListeners.containsKey(destinationSlot)) {
//            MincraLogger.debug("[onClick] clickListeners.get(slot).size: " + clickListeners.get(slot).size());
            for (Consumer<InventoryClickEvent> listener : clickListeners.get(destinationSlot)) {
//                MincraLogger.debug("[onClick] clickListeners.get(slot)");
                listener.accept(event);
//                MincraLogger.debug("[onClick] after listener.accept(event) states: " + states);
            }
        }

        // Global listeners
        for (Consumer<InventoryClickEvent> listener : clickListeners.getOrDefault(-1, new ArrayList<>())) {
            listener.accept(event);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inv)) return;

        for (int slot : event.getRawSlots()) {
            if (slot >= event.getView().getTopInventory().getSize())
                continue; // Skip if the slot is in the player's inventory

            if (!isModifiableSlot.test(slot)) {
                event.setCancelled(true);
                return;
            }

            if (dragListeners.containsKey(slot)) {
                for (Consumer<InventoryDragEvent> listener : dragListeners.get(slot)) {
                    listener.accept(event);
                }
            }
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inv)) return;

        for (Consumer<InventoryCloseEvent> listener : closeListeners) {
            listener.accept(event);
        }
    }
}

record UpdateQueueItem<T>(
        int index,
        T newValue
) {
}

/**
 * State 更新の待ちキュー
 */
class UpdateQueue {
    private final List<UpdateQueueItem<?>> queue = new ArrayList<>();

    public <T> void add(int index, T newValue) {
        queue.add(new UpdateQueueItem<>(index, newValue));
    }

    public void apply(Function<UpdateQueueItem<?>, Void> updater) {
        for (UpdateQueueItem<?> item : queue) {
            updater.apply(item);
        }
        queue.clear();
    }
}
