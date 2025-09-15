package jp.mincra.mincramagics.nbt.utils;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class ItemMetas {
    /**
     * アイテムの最大耐久値を取得する
     * 1.21.4 以降のカスタム耐久値対応
     * @param item 耐久値を取得したいアイテム
     * @return アイテムの最大耐久値
     */
    public static int getMaxDamage(ItemStack item) {
        final var maxDamage = item.getData(DataComponentTypes.MAX_DAMAGE);

        if (maxDamage != null) {
            return maxDamage;
        }

        return item.getType().getMaxDurability();
    }
}
