package jp.mincra.mincramagics.oraxen.mechanic;

public enum RecipeType {
    /**
     * バニラレシピ
     * 参照: <a href="https://minecraft.fandom.com/ja/wiki/%E3%83%AC%E3%82%B7%E3%83%94_(%E3%82%B7%E3%82%B9%E3%83%86%E3%83%A0)#crafting_shaped">...</a>
     */
    CRAFTING_SHAPED,
    CRAFTING_SHAPELESS,
    BLASTING,
    SMELTING,
    SMITHING,
    SMOKING,
    STONE_CUTTING,
    /**
     * 装備のアップグレード
     */
    CRAFTING_SPECIAL_UPGRADE;

    public static RecipeType fromString(String text) {
        for (RecipeType t : RecipeType.values()) {
            if (t.toString().equalsIgnoreCase(text.toUpperCase())) {
                return t;
            }
        }
        return null;
    }
}
