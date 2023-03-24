package jp.mincra.mincramagics.hud;

import jp.mincra.mincramagics.player.SkillCooldown;
import org.bukkit.Bukkit;

import java.util.Map;

public class CooldownHudController {
    // region oraxen glyph code
    private static final Map<Integer, String> NUM_TO_IDENTIFIER = Map.of(
            0, "%oraxen_mini_number_0%",
            1, "%oraxen_mini_number_1%",
            2, "%oraxen_mini_number_2%",
            3, "%oraxen_mini_number_3%",
            4, "%oraxen_mini_number_4%",
            5, "%oraxen_mini_number_5%",
            6, "%oraxen_mini_number_6%",
            7, "%oraxen_mini_number_7%",
            8, "%oraxen_mini_number_8%",
            9, "%oraxen_mini_number_9%"
    );
    private static final String DOT = "%oraxen_mini_number_dot%";
    private static final String NEG_SHIFT_16 = "%oraxen_neg_shift_16%";
    private static final String NEG_SHIFT_12 = "%oraxen_neg_shift_12%";
    private static final String NEG_SHIFT_10 = "%oraxen_neg_shift_10%";
    private static final String NEG_SHIFT_4 = "%oraxen_neg_shift_4%";
    private static final String NEG_SHIFT_2 = "%oraxen_neg_shift_2%";
    private static final String SHIFT = "%oraxen_shift_8%";
    //endregion

    public String generateCooldownHud(SkillCooldown skillCooldown) {
        Map<String, Integer> materialIdToCooldownEndTick = skillCooldown.materialIdToCooldownEndTick();
        StringBuilder hudBuilder = new StringBuilder();

        for (String materialId : materialIdToCooldownEndTick.keySet()) {
            if (!hudBuilder.isEmpty()) {
                hudBuilder.append(SHIFT);
            }

            int cooldownEndAtTick = materialIdToCooldownEndTick.get(materialId);
            int currentTick = Bukkit.getCurrentTick();
            float cooldown = ((float)(cooldownEndAtTick - currentTick)) / 20;

            if (cooldown == 0) {
                // Cooldown終わってたら飛ばす
                continue;
            }

            String materialPlaceholder = "%oraxen_material_" + materialId + "%";

//            if (!PlaceholderAPI.isRegistered(materialPlaceholder)) {
//                Bukkit.getLogger().warning("CooldownHudController: " + materialPlaceholder + " isn't registered to oraxen.");
//                continue;
//            }

            if (cooldown >= 2) {
                hudBuilder
                        .append(materialPlaceholder) // 12
                        .append(NEG_SHIFT_12); // -12
                StringBuilder numericBuilder = new StringBuilder();
                int intCool = (int) cooldown;
                // 13 など
                while (intCool >= 1) {
                    numericBuilder
                            .insert(0, NUM_TO_IDENTIFIER.get(intCool % 10) + NEG_SHIFT_10);
                    intCool = intCool / 10;
                }
                 hudBuilder
                         .append(numericBuilder)
                         .append(NEG_SHIFT_4);
            } else {
                // 0.5 など
                int beforeDecimal = (int) cooldown % 10;
                int afterDecimal = (int) (cooldown * 10) % 10;
                hudBuilder
                        .append(materialPlaceholder) // 12
                        .append(NEG_SHIFT_16) // -12
                        .append(NUM_TO_IDENTIFIER.get(beforeDecimal)) // 12
                        .append(NEG_SHIFT_10) // -10
                        .append(DOT) // 12
                        .append(NEG_SHIFT_10) // -10
                        .append(NUM_TO_IDENTIFIER.get(afterDecimal)) // 12
                        .append(NEG_SHIFT_12) // -16
                        .append(NEG_SHIFT_2);
                // SUM = 0
            }
        }

        return hudBuilder.toString();
    }
}

/**
 *
 * @param materialId クールダウン中のマテリアルのID. ここで指定したマテリアルのglyphが使われる.
 * @param cooldown 秒単位のクールダウン
 */
record CooldownProperty(String materialId, float cooldown) {}