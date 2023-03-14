package jp.mincra.hud;

import jp.mincra.core.MP;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

public class MpHudController {
    private static final String noMpIconCode = "%oraxen_mana_zero%";
    private static final String halfMpIconCode = "%oraxen_mana_half%";
    private static final String fullMpIconCode = "%oraxen_mana_full%";
    private static final String shiftCode = "%oraxen_shift_mana%";

    public void displayMpBar(Player player, MP mp) {
        StringBuilder builder = new StringBuilder();
        int maxMp = (int)mp.getMaxMp();
        int currentMp = (int)mp.getMp();
        int mpDiff = maxMp - currentMp;

        int noMpLen = mpDiff / 2;
        int halfMpLen = (mpDiff & 1); //奇数なら1 偶数なら0
        int fullMpLen = currentMp / 2;

        builder.append(shiftCode);

        for (int i = 0; i < noMpLen; i++) {
            builder.append(noMpIconCode);
        }
        for (int i = 0; i < halfMpLen; i++) {
            builder.append(halfMpIconCode);
        }
        for (int i = 0; i < fullMpLen; i++) {
            builder.append(fullMpIconCode);
        }

        String display = PlaceholderAPI.setPlaceholders(player, builder.toString());
        TextComponent component = Component
                .text(display);
        player.sendActionBar(component);
    }
}
