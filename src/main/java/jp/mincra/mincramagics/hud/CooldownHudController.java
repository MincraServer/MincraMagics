package jp.mincra.mincramagics.hud;

import jp.mincra.mincramagics.player.SkillCooldown;
import org.bukkit.Bukkit;

import java.util.Map;

public class CooldownHudController {
    // region oraxen glyph code
    private static final Map<Integer, String> NUM_TO_IDENTIFIER = Map.of(
            0, "%oraxen_hud_mini_number_0%",
            1, "%oraxen_hud_mini_number_1%",
            2, "%oraxen_hud_mini_number_2%",
            3, "%oraxen_hud_mini_number_3%",
            4, "%oraxen_hud_mini_number_4%",
            5, "%oraxen_hud_mini_number_5%",
            6, "%oraxen_hud_mini_number_6%",
            7, "%oraxen_hud_mini_number_7%",
            8, "%oraxen_hud_mini_number_8%",
            9, "%oraxen_hud_mini_number_9%"
    );
    private static final String DOT = "%oraxen_hud_mini_number_dot%";
    private static final String NEG_SHIFT_16 = "%oraxen_neg_shift_16%";
    private static final String NEG_SHIFT_14 = "%oraxen_neg_shift_14%";
    private static final String NEG_SHIFT_12 = "%oraxen_neg_shift_12%";
    private static final String NEG_SHIFT_10 = "%oraxen_neg_shift_10%";
    private static final String NEG_SHIFT_1 = "%oraxen_neg_shift_1%";
    private static final String SHIFT_1 = "%oraxen_shift_1%";
    //endregion

    public String generateCooldownHud(SkillCooldown skillCooldown) {
        Map<String, Integer> materialIdToCooldownEndTick = skillCooldown.materialIdToCooldownEndTick();
        StringBuilder hudBuilder = new StringBuilder();
        int iconCount = 0;

        for (String materialId : materialIdToCooldownEndTick.keySet()) {
            int cooldownEndAtTick = materialIdToCooldownEndTick.get(materialId);
            int currentTick = Bukkit.getCurrentTick();
            float cooldown = ((float)(cooldownEndAtTick - currentTick)) / 20;

            if (cooldown <= 0) {
                // Cooldown終わってたら飛ばす
                continue;
            }

            String materialPlaceholder = "%oraxen_hud_material_" + materialId + "%";

            // アイコンが未登録なら表示しない?
//            if (!PlaceholderAPI.isRegistered(materialPlaceholder)) {
//                Bukkit.getLogger().warning("CooldownHudController: " + materialPlaceholder + " isn't registered to oraxen.");
//                continue;
//            }

            // アイコンの数を記録
            iconCount++;

            // 十の位
            int cooldownTens = ((int)(cooldown / 10)) % 10;
            // 一の位
            int cooldownOnes = (int)(cooldown) % 10;
            // 小数点第一位
            int cooldownFirstDecimal = (int) (cooldown * 10) % 10;

            if (cooldownTens > 0) {
                // cooldownが2ケタのとき
                hudBuilder
                        .append(materialPlaceholder)
                        .append(NEG_SHIFT_14)
                        .append(NUM_TO_IDENTIFIER.get(cooldownTens))
                        .append(NEG_SHIFT_10)
                        .append(NUM_TO_IDENTIFIER.get(cooldownOnes))
                        .append(NEG_SHIFT_1);
            } else if (cooldownOnes >= 3) {
                // cooldownが3秒以上のとき
                hudBuilder
                        .append(materialPlaceholder)
                        .append(NEG_SHIFT_12)
                        .append(NUM_TO_IDENTIFIER.get(cooldownOnes));
            } else {
                // cooldownが0~3秒のとき
                hudBuilder
                        .append(materialPlaceholder)
                        .append(NEG_SHIFT_16)
                        .append(NUM_TO_IDENTIFIER.get(cooldownOnes))
                        .append(NEG_SHIFT_10)
                        .append(DOT)
                        .append(NEG_SHIFT_10)
                        .append(NUM_TO_IDENTIFIER.get(cooldownFirstDecimal))
                        .append(NEG_SHIFT_1);
            }
        }

        if (iconCount > 0) {
            // CooldownHUDだけを1ピクセル左に寄せる
            hudBuilder.insert(0, NEG_SHIFT_1)
                    .append(SHIFT_1);
        }

        return hudBuilder.append(NEG_SHIFT_12.repeat(iconCount)).toString();
    }
}