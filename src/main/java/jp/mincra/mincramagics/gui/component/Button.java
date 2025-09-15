package jp.mincra.mincramagics.gui.component;

import jp.mincra.mincramagics.gui.lib.Position;
import lombok.Builder;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Builder
public class Button extends BaseButton {
    private Position pos;
    private ItemStack icon;
    private Consumer<Void> onClick;

    public Button(Position pos, ItemStack icon, Consumer<Void> onClick) {
        super(pos, icon, onClick);
    }
}
