package jp.mincra.hud;

import jp.mincra.core.MP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public class MpHudController {
    private static final String noMpIconCode = "〇";
    private static final String halfMpIconCode = "◎";
    private static final String fullMpIconCode = "●";

    public void displayMpBar(Player player, MP mp) {
        StringBuilder builder = new StringBuilder();
        int maxMp = (int)mp.getMaxMp();
        int currentMp = (int)mp.getMp();
        int mpDiff = maxMp - currentMp;

        int noMpLen = mpDiff / 2;
        int halfMpLen = (mpDiff & 1); //奇数なら1 偶数なら0
        int fullMpLen = currentMp / 2;

        for (int i = 0; i < noMpLen; i++) {
            builder.append(noMpIconCode);
        }
        for (int i = 0; i < halfMpLen; i++) {
            builder.append(halfMpLen);
        }
        for (int i = 0; i < fullMpLen; i++) {
            builder.append(fullMpLen);
        }

        TextComponent component = Component
                .text(builder.toString())
                .color(TextColor.fromHexString("#9CE9E6"));
        player.sendActionBar(component);
    }
}
