package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
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

        // PlaySound
        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 4F);

        // Play Vfx
        Location _playerLoc = playerLoc.clone();
        if (vfxManager == null) vfxManager = MincraMagics.getVfxManager();
        Vfx vfx = vfxManager.getVfx("inferno");
        vfx.playEffect(_playerLoc.add(new Vector(0, 0.5, 0)), 5,
                        new Vector(0, 1, 0), Math.PI * player.getEyeLocation().getYaw() / 180);

        Vector spawnRelativeLoc = new Vector(0, 3, 0);

        // Start repeating
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // Shoot Fireball
                    Location eye = player.getEyeLocation();
                    Location targetBlock = player.getTargetBlock(null, 50).getLocation();
                    Location spawnAt = eye.add(eye.getDirection().multiply(1.2)).add(spawnRelativeLoc);
                    Fireball fireball = (Fireball) spawnAt.getWorld()
                            .spawnEntity(spawnAt, EntityType.FIREBALL);
                    fireball.setVelocity(targetBlock.clone().subtract(spawnAt)
                            .toVector().normalize());
                    fireball.setShooter(player);

                    // Sound;
                    world.playSound(playerLoc, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

                    // Vfx
                    vfx.playEffect(targetBlock.add(new Vector(0, 1, 0)), 5);

                    // Fireball を上に
//                    spawnRelativeLoc.add(new Vector(0, 1.5, 0));
                })
                .repeat(TickTime.TICK, 5, 5, 5)
                .run();
    }
}
