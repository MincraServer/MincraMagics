package jp.mincra.mincramagics.hud;

import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.util.ARGBLike;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Range;

import java.util.Collection;

public class HudManager implements Listener {
    private final PlayerManager playerManager;
    private final MpHudController mpHudController = new MpHudController();
    private final CooldownHudController cooldownHudController = new CooldownHudController();

    private Collection<MincraPlayer> players;

    private static final String SHIFT_HUD = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_hud%");
    private static final String SHIFT_COOLDOWN = PlaceholderAPI.setPlaceholders(null, "%oraxen_shift_cooldown%");
    private static final String NEG_SHIFT_COOLDOWN = PlaceholderAPI.setPlaceholders(null, "%oraxen_neg_shift_cooldown%");

    public HudManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        players = playerManager.getPlayers();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(MincraMagics.getInstance(), () -> {
            for (MincraPlayer mPlayer : players) {
                String mpHud = mpHudController.generateMpBar((int) mPlayer.getMaxMp(), (int) mPlayer.getMp());
                String cooldownHud = cooldownHudController.generateCooldownHud(mPlayer.getCooldown());

                Player player = mPlayer.getPlayer();
                TextComponent component = Component
                        .text(SHIFT_HUD + SHIFT_COOLDOWN + cooldownHud + NEG_SHIFT_COOLDOWN + mpHud).shadowColor(new Transparent());
                player.sendActionBar(component);
            }
        }, 0L, 2L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        players = playerManager.getPlayers();
    }
}

class Transparent implements ARGBLike {

    @Override
    public @Range(from = 0L, to = 255L) int alpha() {
        return 0;
    }

    @Override
    public @Range(from = 0L, to = 255L) int red() {
        return 0;
    }

    @Override
    public @Range(from = 0L, to = 255L) int green() {
        return 0;
    }

    @Override
    public @Range(from = 0L, to = 255L) int blue() {
        return 0;
    }
}