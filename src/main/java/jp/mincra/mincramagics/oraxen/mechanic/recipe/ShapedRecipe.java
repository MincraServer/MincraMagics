//package jp.mincra.mincramagics.mechanics;
//
//import java.util.Map;
//
//class ShapedRecipe {
//    // CRAFTING_SHAPED
//    /** レシピの形 */
//    private final String pattern;
//    /** patternで指定した文字に対するアイテムIDまたはタグ
//     * map-list:
//     * - id: thing
//     *   blah: blah
//     * - id: another thing
//     *   blah: blahblah
//     * */
//    private final Map<Character, RecipeIngredient> keyMap;
//    /** クラフト結果のアイテムID */
//    private final String result;
//
//    ShapedRecipe(String pattern, Map<Character, RecipeIngredient> keyMap, String result) {
//        this.pattern = pattern;
//        this.keyMap = keyMap;
//        this.result = result;
//    }
//
//    public String getPattern() {
//        return pattern;
//    }
//
//    public Map<Character, RecipeIngredient> getKeyMap() {
//        return keyMap;
//    }
//
//    public String getResult() {
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        return "ShapedRecipe{" +
//                "pattern='" + pattern + '\'' +
//                ", keyMap=" + keyMap +
//                ", result='" + result + '\'' +
//                '}';
//    }
//}