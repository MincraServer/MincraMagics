package jp.mincra.mincramagics.gui.component;

import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.gui.lib.Component;
import jp.mincra.mincramagics.gui.lib.Position;
import jp.mincra.mincramagics.utils.Strings;
import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

@Builder
public class ItemSlot extends Component {
    private final Position pos;
    private final Function<ItemStack, Boolean> onItemPlaced;
    private final Consumer<Boolean> onItemPickedUp;
    private final ItemStack item;

    /**
     *
     * @param pos
     * @param onItemPlaced
     * @param onArtifactPickedUp
     * @param item Material.DEBUG_STICK なら setItem(null)
     */
    public ItemSlot(Position pos, Function<ItemStack, Boolean> onItemPlaced, Consumer<Boolean> onArtifactPickedUp, ItemStack item) {
        this.pos = pos;
        this.onItemPlaced = onItemPlaced;
        this.onItemPickedUp = onArtifactPickedUp;
        this.item = item;
    }

    @Override
    public void render(Inventory inv) {
        final var currentItem = inv.getItem(pos.startIndex());
        MincraLogger.debug("[ItemSlot] item: " + Strings.truncate(item));
        if (item != null && !item.getType().equals(Material.AIR) && currentItem != null && currentItem.getType() != Material.AIR) {
            inv.setItem(pos.startIndex(), item);
        }
        if (item != null && item.getType().equals(Material.DEBUG_STICK)) {
            inv.setItem(pos.startIndex(), null);
        }
        addMoveToOtherInventoryListener(pos.startIndex(), (e) ->
                e.event().setCancelled(!onItemPlaced.apply(e.item())));
        addPlaceListener(pos.startIndex(), (e) ->
                e.event().setCancelled(!onItemPlaced.apply(e.item())));
        addPickupListener(pos.startIndex(), (e) ->
                onItemPickedUp.accept(true));
        addSwapListener(pos.startIndex(), (e) ->
                // TODO: Artifact の入れ替えに対応する
                e.event().setCancelled(true));
        addClickListener(e -> {
            // When move to player's inventory from GUI
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getRawSlot() == pos.startIndex()) {
                onItemPickedUp.accept(true);
            }
        });
    }
}