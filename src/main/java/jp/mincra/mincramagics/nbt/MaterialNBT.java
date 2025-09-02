package jp.mincra.mincramagics.nbt;

import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.mincramagics.nbt.components.Divider;

import java.util.ArrayList;
import java.util.List;

public record MaterialNBT(double cooldown, double mp) {
    public ItemBuilder setNBTTag(ItemBuilder builder) {
        List<String> oldLore = builder.getLore();
        List<String> newLore = new ArrayList<>(oldLore);

        String divider = Divider.toString(oldLore);
        newLore.add(divider);

        // Cooldown, MP 表示
        newLore.add(Color.COLOR_WHITE + "消費マナ       " + Color.COLOR_YELLOW + String.format( "%.1f", mp ));
        newLore.add(Color.COLOR_WHITE + "クールダウン  " + Color.COLOR_YELLOW + String.format( "%.1f", cooldown ));

        newLore.add(divider);

        builder.setLore(newLore);
        return builder;
    }
}
