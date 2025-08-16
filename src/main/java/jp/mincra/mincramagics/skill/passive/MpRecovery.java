package jp.mincra.mincramagics.skill.passive;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.utils.Functions;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class MpRecovery extends MagicSkill {
    private static final String METADATA_KEY = "mp_recovery";

    private final Logger logger = MincraMagics.getPluginLogger();
    private final PlayerManager playerManager = MincraMagics.getPlayerManager();

    @Override
    public void onEquip(Player player, MaterialProperty property) {
        super.onEquip(player, property);

        // Parameters
        final double level = property.level();
        final double recoveryRate = 1.0f;
        // level1 = 200 tick, level2 = 198 tick, ...
        final double recoveryInterval = 302.5 + Functions.logistic(level, 100, 0.3, 13);

        // Effect and sound
        Vfx vfx = vfxManager.getVfx("mana_charge");
        Location playerLoc = player.getLocation();
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        player.playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1F);

        player.setMetadata(METADATA_KEY, new FixedMetadataValue(MincraMagics.getInstance(), true));

        MincraPlayer mincraPlayer = playerManager.getPlayer(player.getUniqueId());
        if (mincraPlayer == null) {
            logger.warning("Player " + player.getName() + " is not registered in PlayerManager.");
            return;
        }

        // Core functionality
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    if (!player.hasMetadata(METADATA_KEY)) {
                        return false; // Stop if the player has unequipped the skill
                    }

                    // heal recoveryRate MP
                    mincraPlayer.addMp((float) recoveryRate, false);
                    return true;
                })
                .repeat(TickTime.TICK, (long) recoveryInterval, 0, -1) // Repeat every recoveryInterval ticks
                .run();
    }

    @Override
    public void onUnequip(Player player, MaterialProperty property) {
        super.onUnequip(player, property);

        if (player.hasMetadata(METADATA_KEY)) {
            player.removeMetadata(METADATA_KEY, MincraMagics.getInstance());
        } else {
            logger.warning("Player " + player.getName() + " does not have hp_recovery metadata.");
        }
    }
}
