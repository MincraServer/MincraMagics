//package jp.mincra.oraxen.mechanics;
//
//import org.bukkit.inventory.ItemStack;
//
//import java.util.Arrays;
//
//class RecipeIngredientMatrix {
//    private final RecipeIngredient[] ingredients;
//
//    public RecipeIngredientMatrix(RecipeIngredient[] ingredients) {
//        this.ingredients = ingredients;
//    }
//
//    public boolean isAvailable(ItemStack[] matrix) {
//        boolean available = true;
//
//        for (int i = 0; i < ingredients.length; i++) {
//            RecipeIngredient ing = ingredients[i];
//            if (!ing.isCompatible(matrix[i])) available = false;
//        }
//
//        return available;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        RecipeIngredientMatrix that = (RecipeIngredientMatrix) o;
//        return Arrays.equals(ingredients, that.ingredients);
//    }
//
//    @Override
//    public int hashCode() {
//        return Arrays.hashCode(ingredients);
//    }
//}
