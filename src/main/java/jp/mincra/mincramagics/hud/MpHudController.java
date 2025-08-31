package jp.mincra.mincramagics.hud;

import me.clip.placeholderapi.PlaceholderAPI;

import java.util.List;

public class MpHudController {
    private static final List<String> noMpIconCode = List.of(
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_zero_1%%oraxen_neg_shift_2%"),
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_zero_2%%oraxen_neg_shift_2%"),
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_zero_3%%oraxen_neg_shift_2%")
    );
    private static final List<String> halfMpIconCode = List.of(
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_half_1%%oraxen_neg_shift_2%"),
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_half_2%%oraxen_neg_shift_2%"),
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_zero_3%%oraxen_neg_shift_2%")
    );
    private static final List<String> fullMpIconCode = List.of(
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_full_1%%oraxen_neg_shift_2%"),
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_full_2%%oraxen_neg_shift_2%"),
            PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_zero_3%%oraxen_neg_shift_2%")
    );
    private static final String shift = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_mana%");
    private static final String negShift = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_mana%");

    public String generateMpBar(int maxMp, int currentMp, int offset) {
        // s: shift, t: neg_shift, f: full1, g: full2, n: no_mp1, m: no_mp2, h: half_mp1, i: half_mp2
        // 10/10: sssssfffff
        // 20/20: ffffffffff
        // 10/20: nnnnnfffff
        // 11/20: nnnnnhffff
        // 30/30: sssssgggggttttttttttffffffffff
        // 36/36: ssffffffffttttttttttffffffffff
        // 37/37: shffffffffttttttttttffffffffff
        // 36/37: snffffffffttttttttttffffffffff
        // 7/37:  snnnnnnnnnttttttttttnnnnnnhfff
        // 40/40: ggggggggggttttttttttffffffffff
        // 55/55
        // 55:60: sshfffffffttttttttttffffffffffttttttttttffffffffff

        int padding = 10 - (((maxMp + 1) % 20) / 2);
        int line = (maxMp + 19) / 20;
        StringBuilder result = new StringBuilder(shift.repeat(padding));

        for (int i = line - 1; i >= 0; i--) {
            int currentMpForLine = Math.min(20, Math.max(0, currentMp - i * 20)); // 11
            int maxMpForLine = Math.min(20, Math.max(0, 2 * ((1 + maxMp - i * 20) / 2))); // 15
            int mpDiffForLine = maxMpForLine - currentMpForLine; // 4

            int noMpLen = mpDiffForLine / 2; // 2
            int halfMpLen = (currentMpForLine & 1); //奇数なら1 偶数なら0 // 1
            int fullMpLen = currentMpForLine / 2; // 5

            result.append(noMpIconCode.get(i + offset).repeat(Math.max(0, noMpLen)))
                  .append(halfMpIconCode.get(i + offset).repeat(halfMpLen))
                  .append(fullMpIconCode.get(i + offset).repeat(fullMpLen));

            if (i > 0) {
                result.append(negShift.repeat(10));
            }
        }

//        MincraMagics.getPluginLogger().info(String.format("padding: %d, line: %d, maxMp: %d, currentMp: %d, result: %s",
//                padding, line, maxMp, currentMp, result));
        return result.toString();
    }
}
