package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.core.MincraPlayer;
import jp.mincra.mincramagics.core.PlayerManager;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Inferno extends MagicSkill {
    private PlayerManager playerManager;
    private VfxManager vfxManager;

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        if (playerManager == null) playerManager = MincraMagics.getPlayerManager();
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());

        // MP, Cooldown
        if (!canTrigger(mPlayer, property)) {
            return;
        }
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 4F);

        // Play Vfx
        Location _playerLoc = playerLoc.clone();
        if (vfxManager == null) vfxManager = MincraMagics.getVfxManager();
        vfxManager.getVfx("inferno")
                .playEffect(_playerLoc.add(new Vector(0, 0.2, 0)), 3);

        Vector eyeDirection = player.getEyeLocation().getDirection().normalize();
        Location spawnLoc = playerLoc.add(new Vector(0, 5, 0));
        Vector velocity = eyeDirection
                .add(new Vector(0, -0.6, 0))
                .multiply(0.7);

        // Start repeating
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // Spawn fireball
                    Fireball fireball = (Fireball) world.spawnEntity(spawnLoc, EntityType.FIREBALL);
                    fireball.setVelocity(velocity);
                    fireball.setShooter(player);

                    // Sound;
                    world.playSound(playerLoc, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
                })
                .repeat(TickTime.TICK, 5, 3, 5)
                .run();
    }
}
