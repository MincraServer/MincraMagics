package jp.mincra.mincramagics.gui.component;

import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.gui.lib.Position;
import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Builder
public class CloseButton extends Button {
    private Position pos;
    private Consumer<Void> onClick;

    public CloseButton(Position pos, Consumer<Void> onClick) {
        super(pos, OraxenItems.exists("gui_close_button") ? OraxenItems.getItemById("gui_close_button").build() : new ItemStack(Material.BARRIER), onClick);
    }
}
