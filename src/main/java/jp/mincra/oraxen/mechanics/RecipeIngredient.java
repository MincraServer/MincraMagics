//package jp.mincra.oraxen.mechanics;
//
//import jp.mincra.item.CustomItemId;
//import jp.mincra.item.TagAccessor;
//import org.bukkit.Material;
//import org.bukkit.Tag;
//import org.bukkit.configuration.serialization.ConfigurationSerializable;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//public class RecipeIngredient implements ConfigurationSerializable {
//    /** Oraxen Id */
//    private final static String ID_KEY = "id";
//    private final static String MATERIAL_KEY = "material";
//    private final static String TAG_KEY = "tag";
//
//    private final CustomItemId itemId;
//    private final Material material;
//    private final Tag<Material> tag;
//
//    public RecipeIngredient(@Nullable String itemId, @Nullable String material, @Nullable String tag) {
//        this.itemId = itemId != null ? new CustomItemId(itemId) : null;
//        this.material = material != null ? Material.getMaterial(material.toUpperCase()) : null;
//        this.tag = tag != null ? TagAccessor.getMaterialTag(tag) : null;
//    }
//
//    public RecipeIngredient(@Nullable CustomItemId itemId, @Nullable Material material) {
//        this.itemId = itemId;
//        this.material = material;
//        this.tag = null;
//    }
//
//    @Override
//    public @NotNull Map<String, Object> serialize() {
//        return new HashMap<String, Object>() {{
//            if (itemId != null) put(ID_KEY, itemId.toString());
//            if (material != null) put(MATERIAL_KEY, material.toString().toLowerCase());
//            if (tag != null) put(TAG_KEY, tag.toString());
//        }};
//    }
//
//    @NotNull
//    public static RecipeIngredient deserialize(@NotNull Map<String, Object> src) {
//        return new RecipeIngredient(
//                (String) src.get(ID_KEY),
//                (String) src.get(MATERIAL_KEY),
//                (String) src.get(TAG_KEY)
//        );
//    }
//
//    /** Checks if ItemStack is compatible with the ingredient.*/
//    public boolean isCompatible(ItemStack itemStack) {
//        if (itemId != null) return Objects.equals(CustomItemId.getIdByItem(itemStack), itemId);
//        if (material != null) return Objects.equals(itemStack.getType(), material);
//        if (tag != null) return tag.isTagged(itemStack.getType());
//        return false;
//    }
//
//    public boolean containsTag() {
//        return tag != null;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        RecipeIngredient that = (RecipeIngredient) o;
//        return Objects.equals(itemId, that.itemId)
//                && material == that.material
//                && Objects.equals(tag, that.tag);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(itemId, material, tag);
//    }
//
//    @Override
//    public String toString() {
//        return "RecipeIngredient{" +
//                "itemId=" + itemId +
//                ", material=" + material +
//                ", tag=" + tag +
//                '}';
//    }
//}