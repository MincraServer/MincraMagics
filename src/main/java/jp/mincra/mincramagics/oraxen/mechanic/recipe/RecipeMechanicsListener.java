//package jp.mincra.mincramagics.mechanics;
//
//import io.th0rgal.oraxen.api.OraxenItems;
//import jp.mincra.mincramagics.MincraMagics;
//import jp.mincra.mincramagics.item.CustomItemId;
//import jp.mincra.mincramagics.utils.Pair;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.inventory.PrepareItemCraftEvent;
//import org.bukkit.inventory.CraftingInventory;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.*;
//
//public class RecipeMechanicsListener implements Listener {
//
//    private final RecipeMechanicFactory factory;
//
//    // Shaped Recipe
//    // 材料をタグで指定しているレシピ. 軽量化の目的で分けている。
//    private final List<Pair<RecipeIngredientMatrix, String>> tagShapedDic = new ArrayList<>();
//    // 材料をタグで指定していないレシピ
//    private final Map<RecipeIngredientMatrix, String> nonTagShapedDic = new HashMap<>();
//
//    public RecipeMechanicsListener(RecipeMechanicFactory factory) {
//        this.factory = factory;
//
//        List<RecipeMechanic> mechanics = factory.getMechanics();
//        for (RecipeMechanic mechanic : mechanics) {
//            switch (mechanic.getType()) {
//                case CRAFTING_SHAPED:
//                    ShapedRecipe shapedRecipe = mechanic.getShapedRecipe();
//
//                    // Create recipe shape matrix
//                    char[] pattern = shapedRecipe.getPattern().toCharArray();
//                    Map<Character, RecipeIngredient> keyMap = shapedRecipe.getKeyMap();
//                    RecipeIngredient[] rawMatrix = new RecipeIngredient[9];
//                    boolean containsTag = false;
//
//                    for (int i = 0; i < 9; i++) {
//                        char key = pattern[i];
//                        rawMatrix[i] = keyMap.get(key);
//                        if (rawMatrix[i].containsTag()) containsTag = true;
//                    }
//
//                    RecipeIngredientMatrix keyMatrix = new RecipeIngredientMatrix(rawMatrix);
//
//                    if (containsTag) {
//                        tagShapedDic.add(new Pair(keyMatrix, shapedRecipe.getResult()));
//                    } else {
//                        nonTagShapedDic.put(keyMatrix, shapedRecipe.getResult());
//                    }
//            }
//        }
//    }
//
//    @EventHandler
//    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
//        CraftingInventory inv = event.getInventory();
//        ItemStack[] matrix = inv.getMatrix();
//
//        MincraMagics.getInstance().getLogger().info("onPrepareItemCraft");
//
//        searchShapedRecipe(inv, matrix);
//    }
//
//    private void searchShapedRecipe(CraftingInventory inv, ItemStack[] matrix) {
//        // Search non tag shaped recipe
//        RecipeIngredient[] rawMatrix = new RecipeIngredient[9];
//        for (int i = 0; i < 9; i++) {
//            ItemStack item = matrix[i];
//            CustomItemId itemId = CustomItemId.getIdByItem(item);
//            if (itemId != null) {
//                rawMatrix[i] = new RecipeIngredient(itemId, null);
//            } else {
//                rawMatrix[i] = new RecipeIngredient(null, item.getType());
//            }
//        }
//        RecipeIngredientMatrix keyMatrix = new RecipeIngredientMatrix(rawMatrix);
//        ItemStack result = OraxenItems.getItemById(nonTagShapedDic.get(keyMatrix)).build();
//        if (result != null) {
//            inv.setResult(result);
//            return;
//        }
//
//        // Search tag shaped recipe
//        for (Pair<RecipeIngredientMatrix, String> matrixToResult : tagShapedDic) {
//            if (matrixToResult.getLeft().isAvailable(matrix)) {
//                inv.setResult(OraxenItems.getItemById(matrixToResult.getRight()).build());
//                return;
//            }
//        }
//    }
//}