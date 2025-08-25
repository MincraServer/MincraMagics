package jp.mincra.mincramagics.gui;

import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.gui.lib.GUI;
import jp.mincra.mincramagics.gui.lib.GUIHelper;
import jp.mincra.mincramagics.gui.lib.GuiComponent;
import jp.mincra.mincramagics.gui.lib.State;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class InventoryGUI implements Listener {
    protected Player player;
    private final Map<Integer, List<Consumer<InventoryClickEvent>>> clickListeners = new HashMap<>();

    private final List<Object> states = new ArrayList<>();
    private final UpdateQueue updateQueue = new UpdateQueue();
    private int lastStateIndex = -1;
    private boolean lockRerendering = false;
    private Predicate<Integer> isModifiableSlot = (index) -> false;

    @Nullable
    private String prevTitle = null;

    public InventoryGUI() {
    }

    // API
    public abstract Inventory getInventory();

    public final void open(Player player) {
        this.player = player;
        render();
    }

    // Rendering
    @Nullable
    protected abstract GUI build();

    protected final void addClickListener(int index, Consumer<InventoryClickEvent> listener) {
        if (!clickListeners.containsKey(index)) {
            clickListeners.put(index, new ArrayList<>());
        }
        clickListeners.get(index).add(listener);
    }

    protected final  <T> State<T> useState(T initialValue) {
        final int thisIndex = ++lastStateIndex;
        final T currentState = getStateOrDefault(thisIndex, initialValue);
        if (thisIndex < states.size()) {
            states.set(thisIndex, currentState);
        } else {
            states.add(currentState);
        }

        return new State<>(currentState, (setter) -> {
            final T newState = getStateOrDefault(thisIndex, initialValue);
            final T updatedState = setter.apply(newState);
            if (lockRerendering) {
                updateQueue.add(thisIndex, updatedState);
                return null;
            }
            states.set(thisIndex, updatedState);
            // Rerender the GUI
            render();
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

        final GUI gui = build();
        if (gui == null) return;

        isModifiableSlot = gui.isModifiableSlot();
        if (player.getOpenInventory().getType() != InventoryType.CHEST) {
           player.openInventory(getInventory());
        }
        if (prevTitle == null || !prevTitle.equals(gui.title())) {
            GUIHelper.updateTitle(player, gui.title());
            prevTitle = gui.title();
        }

        for (GuiComponent component : gui.components()) {
            Inventory inv = getInventory();
            component.render(inv);

            for (Map.Entry<Integer, List<Consumer<InventoryClickEvent>>> entry : component.getClickListeners().entrySet()) {
                int index = entry.getKey();
                List<Consumer<InventoryClickEvent>> listeners = entry.getValue();
                for (Consumer<InventoryClickEvent> listener : listeners) {
                    addClickListener(index, listener);
                }
            }
        }

        lockRerendering = false;
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
        if (!event.getInventory().equals(getInventory())) return;

        event.setCancelled(!isModifiableSlot.test(event.getSlot()));

        int slot = event.getSlot();
        if (clickListeners.containsKey(slot)) {
            for (Consumer<InventoryClickEvent> listener : clickListeners.get(slot)) {
                listener.accept(event);
            }
        }
    }
}

record UpdateQueueItem<T>(
        int index,
        T newValue
) {}

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
