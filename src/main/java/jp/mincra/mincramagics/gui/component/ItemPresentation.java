package jp.mincra.mincramagics.gui.component;

import jp.mincra.mincramagics.gui.lib.Component;
import jp.mincra.mincramagics.gui.lib.Position;
import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Builder
public class ItemPresentation extends Component {
    private final Position pos;
    private final ItemStack item;

    public ItemPresentation(Position pos, ItemStack item) {
        this.pos = pos;
        this.item = item;
    }

    @Override
    public void render(Inventory inv) {
        if (item == null || item.getType().equals(Material.AIR)) {
            inv.setItem(pos.startIndex(), Icons.transparent());
        } else {
            inv.setItem(pos.startIndex(), item);
        }
    }
}
