//package jp.mincra.oraxen.mechanics;
//
//import io.th0rgal.oraxen.mechanics.Mechanic;
//import io.th0rgal.oraxen.mechanics.MechanicFactory;
//import jp.mincra.MincraMagics;
//import org.bukkit.NamespacedKey;
//import org.bukkit.configuration.ConfigurationSection;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//public class RecipeMechanic extends Mechanic {
//
//    static NamespacedKey NAMESPACED_KEY = new NamespacedKey(MincraMagics.getInstance(), "type");
//
//    private final RecipeType type;
//    private final String group;
//
//    private ShapedRecipe shapedRecipe;
//
//    public RecipeMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
//        super(mechanicFactory, section);
//
//        MincraMagics.getInstance().getLogger().info("loading RecipeMechanic...");
//
//        RecipeType rawType = RecipeType.fromString(section.getString("type"));
//        assert rawType != null : "Oraxen Recipe Mechanic Type statement is null at:" + section.getCurrentPath();
//        this.type = rawType;
//        this.group = section.getString("group");
//
//        switch (this.type) {
//            case CRAFTING_SHAPED:
//                ConfigurationSection keySection = section.getConfigurationSection("keys");
//                assert keySection != null : "Oraxen Recipe Mechanic Keys statement is null at: " + section.getCurrentPath();
//                this.shapedRecipe = new ShapedRecipe(
//                        String.join("", section.getStringList("pattern")),
//                        deserializeKeyMap(keySection),
//                        section.getString("result", null)
//                );
//        }
//
//
//        MincraMagics.getInstance().getLogger().info("loading RecipeMechanic...");
//    }
//
//    public Map<Character, RecipeIngredient> deserializeKeyMap(ConfigurationSection section) {
//        Set<String> keys = section.getKeys(false);
//        Map<Character, RecipeIngredient> map = new HashMap<>();
//
//        for (String key : keys) {
//            map.put(key.charAt(0),
//                    RecipeIngredient.deserialize(section.getConfigurationSection(key).getValues(false)));
//        }
//
//        return map;
//    }
//
//    public RecipeType getType() {
//        return type;
//    }
//
//    public String getGroup() {
//        return group;
//    }
//
//    public ShapedRecipe getShapedRecipe() { return shapedRecipe; }
//}
//
