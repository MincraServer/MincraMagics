package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Set;

public class Inferno extends MagicSkill {
    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // PlaySound
        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 2F);

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector offset = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("inferno");
        vfx.playEffect(vfxLoc, 5, offset, Math.toRadians(player.getEyeLocation().getYaw()));

        Vector spawnRelativeLoc = new Vector(0, 3, 0);
        int maxDistance = 50;

        // Start repeating
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // Get Location
                    Location eye = player.getEyeLocation();
                    Entity targetEntity = player.getTargetEntity(maxDistance);
                    Location targetLocation;
                    if (targetEntity != null) {
                        targetLocation = targetEntity.getLocation();
                    } else {
                        targetLocation = player.getTargetBlock(
                                Set.of(Material.AIR, Material.CAVE_AIR, Material.SHORT_GRASS, Material.TALL_GRASS),
                                maxDistance).getLocation();
                    }
                    Location spawnAt = eye.add(eye.getDirection().multiply(1.2)).add(spawnRelativeLoc);

                    // Spawn Fireball
                    Fireball fireball = (Fireball) spawnAt.getWorld()
                            .spawnEntity(spawnAt, EntityType.FIREBALL);
                    Vector velocity = targetLocation.clone().subtract(spawnAt).toVector().normalize();
                    fireball.setVelocity(velocity);
                    fireball.setShooter(player);

                    // Sound;
                    world.playSound(playerLoc, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

                    // FIXME: ターゲットブロックにvfxが表示されない
                    vfx.playEffect(targetLocation.add(new Vector(0, 1, 0)), 5);

                    // Fireball を上に
//                    spawnRelativeLoc.add(new Vector(0, 1.5, 0));

                    return true;
                })
                .repeat(TickTime.TICK, 5, 5, 5)
                .run();

        return true;
    }
}
