package jp.mincra.mincramagics.nbtobject;

import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.nbtobject.components.Divider;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record MaterialNBT(double cooldown, double mp) {
    private static final String COLOR_AQUA = "§b";

    public ItemBuilder setNBTTag(ItemBuilder builder) {
        ItemStack item = builder.build();
        List<String> oldLore = builder.getLore();
        List<String> newLore = new ArrayList<>(oldLore);

        String divider = Divider.toString(oldLore);
        newLore.add(divider);

        // Cooldown, MP 表示
        newLore.add(COLOR_AQUA + "MP       " + String.format( "%.1f", mp ));
        newLore.add(COLOR_AQUA + "Cooldown " + String.format( "%.1f", cooldown ));

        newLore.add(divider);

        builder.setLore(newLore);
        builder.regen();
        return builder;
    }
}
