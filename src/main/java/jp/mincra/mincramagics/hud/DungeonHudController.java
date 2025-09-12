package jp.mincra.mincramagics.hud;

import com.civious.dungeonmmo.instances.InstancesManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class DungeonHudController {
    final InstancesManager instancesManager = InstancesManager.getInstance();

    final String SHIFT_1 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_1%");
    final String SHIFT_2 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_2%");
    final String SHIFT_4 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_4%");
    final String SHIFT_8 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_8%");
    final String SHIFT_16 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_16%");
    final String SHIFT_32 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_32%");
    final String SHIFT_64 = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_64%");
    final String NEG_SHIFT_64 = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_64%");
    final String NEG_SHIFT_32 = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_32%");
    final String NEG_SHIFT_16 = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_16%");
    final String NEG_SHIFT_8 = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_8%");
    final String NEG_SHIFT_2 = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_2%");

    public String getDungeonHud(Player player) {
        final var instance = instancesManager.getInstance(player);
        if (instance == null) return "";

        final var runnable = instance.getRunnable();
        if (runnable == null) return "";

        final var sec = runnable.getTimeInSeconds();
        final var min = sec / 60;
        final var life = instance.getCurrentLifes();

        return String.format("%s%s%s§f%02d:%02d §c❤§fx%d", NEG_SHIFT_64, NEG_SHIFT_32, NEG_SHIFT_8, min, sec % 60, life) + SHIFT_32 + SHIFT_16 + SHIFT_8 + SHIFT_2 + SHIFT_1;
    }
}
