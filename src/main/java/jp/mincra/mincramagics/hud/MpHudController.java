package jp.mincra.mincramagics.hud;

import jp.mincra.mincramagics.player.MP;
import me.clip.placeholderapi.PlaceholderAPI;

public class MpHudController {
    private static final String noMpIconCode = PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_zero%");
    private static final String halfMpIconCode = PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_half%");
    private static final String fullMpIconCode = PlaceholderAPI.setPlaceholders(null, "%oraxen_hud_mana_full%");

    public String generateMpBar(MP mp) {
        int maxMp = (int)mp.getMaxMp();
        int currentMp = (int)mp.getMp();
        int mpDiff = maxMp - currentMp;

        int noMpLen = mpDiff / 2;
        int halfMpLen = (mpDiff & 1); //奇数なら1 偶数なら0
        int fullMpLen = currentMp / 2;

        return noMpIconCode.repeat(Math.max(0, noMpLen)) +
                halfMpIconCode.repeat(halfMpLen) +
                fullMpIconCode.repeat(Math.max(0, fullMpLen));
    }
}
