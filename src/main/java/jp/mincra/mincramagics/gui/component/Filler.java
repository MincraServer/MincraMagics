package jp.mincra.mincramagics.gui.component;

import jp.mincra.mincramagics.gui.lib.Component;
import jp.mincra.mincramagics.gui.lib.Position;
import lombok.Builder;
import org.bukkit.inventory.Inventory;

import java.util.function.Predicate;

@Builder
public class Filler extends Component {
    private final Position pos;
    private final Predicate<Integer> isSlotExcluded;

    public Filler(Position pos, Predicate<Integer> isSlotExcluded) {
        this.pos = pos;
        this.isSlotExcluded = isSlotExcluded;
    }

    @Override
    public void render(Inventory inv) {
        pos.toIndexStream().forEach(i -> {
            if (isSlotExcluded.test(i)) return;
            inv.setItem(i, Icons.transparent());
        });
    }
}
