package jp.mincra.mincramagics.gui.component;

import jp.mincra.mincramagics.gui.lib.Component;
import jp.mincra.mincramagics.gui.lib.Position;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class BaseButton extends Component {
    private final Position pos;
    private final ItemStack icon;
    private final Consumer<Void> onClick;

    public BaseButton(Position pos, ItemStack icon, Consumer<Void> onClick) {
        this.pos = pos;
        this.icon = icon;
        this.onClick = onClick;
    }

    @Override
    public void render(Inventory inv) {
        inv.setItem(pos.startIndex(), icon);
        addClickListener(pos.startIndex(), event -> onClick.accept(null));
    }
}
