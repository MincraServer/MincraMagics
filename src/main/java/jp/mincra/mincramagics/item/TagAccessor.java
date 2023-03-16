package jp.mincra.mincramagics.item;

import org.bukkit.Material;
import org.bukkit.Tag;

public class TagAccessor {
    public static Tag<Material> getMaterialTag(String name) {
        switch (name) {
            // Blocks
            case "acacia_logs":
                return Tag.ACACIA_LOGS;
            case "ancient_city_replaceable":
                return Tag.ANCIENT_CITY_REPLACEABLE;
            case "animals_spawnable_on":
                return Tag.ANIMALS_SPAWNABLE_ON;
            case "anvil":
                return Tag.ANVIL;
            case "axolotls_spawnable_on":
                return Tag.AXOLOTLS_SPAWNABLE_ON;
            case "azalea_grows_on":
                return Tag.AZALEA_GROWS_ON;
            case "azalea_root_replaceable":
                return Tag.AZALEA_ROOT_REPLACEABLE;
            case "bamboo_plantable_on":
                return Tag.BAMBOO_PLANTABLE_ON;
            case "banners":
                return Tag.BANNERS;
            case "base_stone_nether":
                return Tag.BASE_STONE_NETHER;
            case "base_stone_overworld":
                return Tag.BASE_STONE_OVERWORLD;
            case "beacon_base_blocks":
                return Tag.BEACON_BASE_BLOCKS;
            case "beds":
                return Tag.BEDS;
            case "beehives":
                return Tag.BEEHIVES;
            case "bee_growables":
                return Tag.BEE_GROWABLES;
            case "big_dripleaf_placeable":
                return Tag.BIG_DRIPLEAF_PLACEABLE;
            case "birch_logs":
                return Tag.BIRCH_LOGS;
            case "buttons":
                return Tag.BUTTONS;
            case "campfires":
                return Tag.CAMPFIRES;
            case "candle_cakes":
                return Tag.CANDLE_CAKES;
            case "candles":
                return Tag.CANDLES;
            case "cauldrons":
                return Tag.CAULDRONS;
            case "climbable":
                return Tag.CLIMBABLE;
            case "coal_ores":
                return Tag.COAL_ORES;
            case "completes_find_tree_tutorial":
                return Tag.COMPLETES_FIND_TREE_TUTORIAL;
            case "convertable_to_mud":
                return Tag.CONVERTABLE_TO_MUD;
            case "copper_ores":
                return Tag.COPPER_ORES;
            case "corals":
                return Tag.CORALS;
            case "coral_blocks":
                return Tag.CORAL_BLOCKS;
            case "coral_plants":
                return Tag.CORAL_PLANTS;
            case "crimson_stems":
                return Tag.CRIMSON_STEMS;
            case "crops":
                return Tag.CROPS;
            case "crystal_sound_blocks":
                return Tag.CRYSTAL_SOUND_BLOCKS;
            case "dampens_vibrations":
                return Tag.DAMPENS_VIBRATIONS;
            case "dark_oak_logs":
                return Tag.DARK_OAK_LOGS;
            case "dead_bush_may_place_on":
                return Tag.DEAD_BUSH_MAY_PLACE_ON;
            case "deepslate_ore_replaceables":
                return Tag.DEEPSLATE_ORE_REPLACEABLES;
            case "diamond_ores":
                return Tag.DIAMOND_ORES;
            case "dirt":
                return Tag.DIRT;
            case "doors":
                return Tag.DOORS;
            case "dragon_immune":
                return Tag.DRAGON_IMMUNE;
            case "dragon_transparent":
                return Tag.DRAGON_TRANSPARENT;
//            case "dripstone_replaceable_blocks":
//            return Tag.DRIPSTONE_REPLACEABLE_BLOCKS;
            case "emerald_ores":
                return Tag.EMERALD_ORES;
            case "enderman_holdable":
                return Tag.ENDERMAN_HOLDABLE;
            case "fall_damage_resetting":
                return Tag.FALL_DAMAGE_RESETTING;
            case "features_cannot_replace":
                return Tag.FEATURES_CANNOT_REPLACE;
            case "fence_gates":
                return Tag.FENCE_GATES;
            case "fences":
                return Tag.FENCES;
            case "fire":
                return Tag.FIRE;
            case "flowers":
                return Tag.FLOWERS;
            case "flower_pots":
                return Tag.FLOWER_POTS;
            case "foxes_spawnable_on":
                return Tag.FOXES_SPAWNABLE_ON;
            case "frogs_spawnable_on":
                return Tag.FROGS_SPAWNABLE_ON;
            case "frog_prefer_jump_to":
                return Tag.FROG_PREFER_JUMP_TO;
            case "gold_ores":
                return Tag.GOLD_ORES;
            case "guarded_by_piglins":
                return Tag.GUARDED_BY_PIGLINS;
            case "hoglin_repellents":
                return Tag.HOGLIN_REPELLENTS;
            case "ice":
                return Tag.ICE;
            case "impermeable":
                return Tag.IMPERMEABLE;
            case "infiniburn_end":
                return Tag.INFINIBURN_END;
            case "infiniburn_nether":
                return Tag.INFINIBURN_NETHER;
            case "infiniburn_overworld":
                return Tag.INFINIBURN_OVERWORLD;
            case "inside_step_sound_blocks":
                return Tag.INSIDE_STEP_SOUND_BLOCKS;
            case "iron_ores":
                return Tag.IRON_ORES;
            case "jungle_logs":
                return Tag.JUNGLE_LOGS;
            case "lapis_ores":
                return Tag.LAPIS_ORES;
            case "lava_pool_stone_cannot_replace":
                return Tag.LAVA_POOL_STONE_CANNOT_REPLACE;
            case "leaves":
                return Tag.LEAVES;
            case "logs":
                return Tag.LOGS;
            case "logs_that_burn":
                return Tag.LOGS_THAT_BURN;
            case "lush_ground_replaceable":
                return Tag.LUSH_GROUND_REPLACEABLE;
            case "mangrove_logs":
                return Tag.MANGROVE_LOGS;
            case "mangrove_logs_can_grow_through":
                return Tag.MANGROVE_LOGS_CAN_GROW_THROUGH;
            case "mangrove_roots_can_grow_through":
                return Tag.MANGROVE_ROOTS_CAN_GROW_THROUGH;
            case "mooshrooms_spawnable_on":
                return Tag.MOOSHROOMS_SPAWNABLE_ON;
            case "moss_replaceable":
                return Tag.MOSS_REPLACEABLE;
            case "mushroom_grow_block":
                return Tag.MUSHROOM_GROW_BLOCK;
            case "needs_diamond_tool":
                return Tag.NEEDS_DIAMOND_TOOL;
            case "needs_iron_tool":
                return Tag.NEEDS_IRON_TOOL;
            case "needs_stone_tool":
                return Tag.NEEDS_STONE_TOOL;
            case "nether_carver_replaceables":
                return Tag.NETHER_CARVER_REPLACEABLES;
//            case "non_flammable_wood":
//            return Tag.NON_FLAMMABLE_WOOD;
            case "nylium":
                return Tag.NYLIUM;
            case "oak_logs":
                return Tag.OAK_LOGS;
            case "occludes_vibration_signals":
                return Tag.OCCLUDES_VIBRATION_SIGNALS;
            case "overworld_carver_replaceables":
                return Tag.OVERWORLD_CARVER_REPLACEABLES;
//            case "overworld_natural_logs":
//            return Tag.OVERWORLD_NATURAL_LOGS;
            case "parrots_spawnable_on":
                return Tag.PARROTS_SPAWNABLE_ON;
            case "piglin_repellents":
                return Tag.PIGLIN_REPELLENTS;
            case "planks":
                return Tag.PLANKS;
            case "portals":
                return Tag.PORTALS;
            case "pressure_plates":
                return Tag.PRESSURE_PLATES;
            case "prevent_mob_spawning_inside":
                return Tag.PREVENT_MOB_SPAWNING_INSIDE;
            case "rabbits_spawnable_on":
                return Tag.RABBITS_SPAWNABLE_ON;
            case "rails":
                return Tag.RAILS;
            case "redstone_ores":
                return Tag.REDSTONE_ORES;
            case "replaceable_plants":
                return Tag.REPLACEABLE_PLANTS;
            case "sand":
                return Tag.SAND;
            case "saplings":
                return Tag.SAPLINGS;
            case "sculk_replaceable":
                return Tag.SCULK_REPLACEABLE;
            case "sculk_replaceable_world_gen":
                return Tag.SCULK_REPLACEABLE_WORLD_GEN;
            case "shulker_boxes":
                return Tag.SHULKER_BOXES;
            case "signs":
                return Tag.SIGNS;
            case "slabs":
                return Tag.SLABS;
            case "small_dripleaf_placeable":
                return Tag.SMALL_DRIPLEAF_PLACEABLE;
            case "small_flowers":
                return Tag.SMALL_FLOWERS;
            case "snaps_goat_horn":
                return Tag.SNAPS_GOAT_HORN;
            case "snow":
                return Tag.SNOW;
            case "snow_layer_can_survive_on":
                return Tag.SNOW_LAYER_CAN_SURVIVE_ON;
            case "soul_fire_base_blocks":
                return Tag.SOUL_FIRE_BASE_BLOCKS;
            case "soul_speed_blocks":
                return Tag.SOUL_SPEED_BLOCKS;
            case "spruce_logs":
                return Tag.SPRUCE_LOGS;
            case "stairs":
                return Tag.STAIRS;
            case "standing_signs":
                return Tag.STANDING_SIGNS;
            case "stone_bricks":
                return Tag.STONE_BRICKS;
            case "stone_ore_replaceables":
                return Tag.STONE_ORE_REPLACEABLES;
            case "stone_pressure_plates":
                return Tag.STONE_PRESSURE_PLATES;
            case "strider_warm_blocks":
                return Tag.STRIDER_WARM_BLOCKS;
            case "tall_flowers":
                return Tag.TALL_FLOWERS;
            case "terracotta":
                return Tag.TERRACOTTA;
            case "trapdoors":
                return Tag.TRAPDOORS;
            case "underwater_bonemeals":
                return Tag.UNDERWATER_BONEMEALS;
            case "unstable_bottom_center":
                return Tag.UNSTABLE_BOTTOM_CENTER;
            case "valid_spawn":
                return Tag.VALID_SPAWN;
            case "walls":
                return Tag.WALLS;
            case "wall_corals":
                return Tag.WALL_CORALS;
            case "wall_post_override":
                return Tag.WALL_POST_OVERRIDE;
            case "wall_signs":
                return Tag.WALL_SIGNS;
            case "warped_stems":
                return Tag.WARPED_STEMS;
            case "wart_blocks":
                return Tag.WART_BLOCKS;
            case "wither_immune":
                return Tag.WITHER_IMMUNE;
            case "wither_summon_base_blocks":
                return Tag.WITHER_SUMMON_BASE_BLOCKS;
            case "wolves_spawnable_on":
                return Tag.WOLVES_SPAWNABLE_ON;
            case "wooden_buttons":
                return Tag.WOODEN_BUTTONS;
            case "wooden_doors":
                return Tag.WOODEN_DOORS;
            case "wooden_fences":
                return Tag.WOODEN_FENCES;
            case "wooden_pressure_plates":
                return Tag.WOODEN_PRESSURE_PLATES;
            case "wooden_slabs":
                return Tag.WOODEN_SLABS;
            case "wooden_stairs":
                return Tag.WOODEN_STAIRS;
            case "wooden_trapdoors":
                return Tag.WOODEN_TRAPDOORS;
            case "wool":
                return Tag.WOOL;
            case "wool_carpets":
                return Tag.WOOL_CARPETS;

            // Items
            case "axolotl_tempt_items":
                return Tag.AXOLOTL_TEMPT_ITEMS;
//            case "beacon_payment_items":
//                return Tag.BEACON_PAYMENT_ITEMS;
//            case "boats":
//                return Tag.BOATS;
//            case "chest_boats":
//                return Tag.CHEST_BOATS;
            case "cluster_max_harvestables":
                return Tag.CLUSTER_MAX_HARVESTABLES;
//            case "coals":
//                return Tag.COALS;
//            case "compasses":
//                return Tag.COMPASSES;
//            case "creeper_drop_music_discs":
//                return Tag.CREEPER_DROP_MUSIC_DISCS;
//            case "fishes":
//                return Tag.FISHES;
            case "fox_food":
                return Tag.FOX_FOOD;
            case "freeze_immune_wearables":
                return Tag.FREEZE_IMMUNE_WEARABLES;
            case "ignored_by_piglin_babies":
                return Tag.IGNORED_BY_PIGLIN_BABIES;
//            case "lectern_books":
//                return Tag.LECTERN_BOOKS;
//            case "music_discs":
//                return Tag.MUSIC_DISCS;
//            case "non_flammable_wood":
//                return Tag.NON_FLAMMABLE_WOOD;
//            case "overworld_natural_logs":
//                return Tag.OVERWORLD_NATURAL_LOGS;
            case "piglin_food":
                return Tag.PIGLIN_FOOD;
//            case "piglin_loved":
//                return Tag.PIGLIN_LOVED;
//            case "stone_crafting_materials":
//                return Tag.STONE_CRAFTING_MATERIALS;
//            case "stone_tool_materials":
//                return Tag.STONE_TOOL_MATERIALS;
        }
        return null;
    }
}
