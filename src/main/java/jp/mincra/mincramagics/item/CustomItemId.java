//package jp.mincra.mincramagics.item;
//
//import io.lumine.mythic.bukkit.MythicBukkit;
//import io.th0rgal.oraxen.api.OraxenItems;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Objects;
//
//public class CustomItemId {
//    private final static String PREFIX_ORAXEN = "oraxen:";
//    private final static String PREFIX_MYTHICMOBS = "mythicmobs:";
//
//    @Nullable
//    private final String oraxenId;
//    @Nullable
//    private final String mythicId;
//
//    /** Constructor of this class.
//     * @param itemId Specify ingredient's item id that is made by custom plugin such as MythicMobs or Oraxen, which should be added to the prefix of id.
//     *               e.g. oraxen:ruby_helmet, mythicmobs:fire_sword
//     * */
//    public CustomItemId(@NotNull String itemId) {
//        oraxenId = itemId.startsWith(PREFIX_ORAXEN) ?
//                itemId.substring(PREFIX_ORAXEN.length()) : null;
//        mythicId = itemId.startsWith(PREFIX_MYTHICMOBS) ?
//                itemId.substring(PREFIX_MYTHICMOBS.length()) : null;
//    }
//
//    public CustomItemId(@Nullable String oraxenId, @Nullable String mythicId) {
//        this.oraxenId = oraxenId;
//        this.mythicId = mythicId;
//    }
//
//    @Nullable
//    public static CustomItemId getIdByItem(ItemStack item) {
//        String oraxenId = OraxenItems.getIdByItem(item);
//        String mythicId = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
//
//        if (oraxenId != null || mythicId != null) {
//            return new CustomItemId(oraxenId, mythicId);
//        }
//        return null;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        CustomItemId that = (CustomItemId) o;
//        return Objects.equals(oraxenId, that.oraxenId)
//                && Objects.equals(mythicId, that.mythicId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(oraxenId, mythicId);
//    }
//
//    @Override
//    public String toString() {
//        if (oraxenId != null) return new StringBuilder(oraxenId).insert(0, PREFIX_ORAXEN).toString();
//        if (mythicId != null) return new StringBuilder(mythicId).insert(0, PREFIX_MYTHICMOBS).toString();
//        return "error";
//    }
//}