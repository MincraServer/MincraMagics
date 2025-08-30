package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Inferno extends MagicSkill implements Listener {
    private final Map<UUID, MagicFireball> fireballs = new HashMap<>();
    private static final String FIREBALL_METADATA = "inferno_fireball";

    private record MagicFireball(Fireball fireball, Player shooter, double damage) {
    }

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final int level = (int) property.level();
        final int maxDistance = 50;
        final double speed = 1.1 - level * 0.2;
        // 直撃ダメージボーナス
        final double hitDamage = 8 * Math.log(level == 0 ? 1 : level) + 4;
        final float yield = 1f + level;
        final int shootCount = 2 * level - 1;
        final int shootIntervalInTick = 5;

        // playSound
        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 2F);

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector offset = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("inferno");
        vfx.playEffect(vfxLoc, 5, offset, Math.toRadians(player.getEyeLocation().getYaw()));

        Vector spawnRelativeLoc = new Vector(0, 3, 0);

        AtomicInteger shootCountAtomic = new AtomicInteger(0);

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
                    Location baseSpawnAt = eye.add(eye.getDirection().multiply(1.2)).add(spawnRelativeLoc);

                    // Spawn Fireball
                    Vector direction = player.getLocation().getDirection();
                    final int attempt = shootCountAtomic.getAndIncrement();

                    // Fireball の位置をずらす
                    Location spawnAt = baseSpawnAt.clone().add(direction.crossProduct(new Vector(0,1,0)).normalize().multiply(
                            2 * (attempt - shootCount / 2)));

                    Fireball fireball = (Fireball) spawnAt.getWorld()
                            .spawnEntity(spawnAt, EntityType.FIREBALL);
                    Vector velocity = targetLocation.clone().subtract(spawnAt).toVector().normalize();
                    fireball.setVelocity(velocity.multiply(speed));
                    fireball.setShooter(player);
                    fireball.setYield(yield);
                    fireball.setMetadata(FIREBALL_METADATA, new FixedMetadataValue(MincraMagics.getInstance(), true));

                    // ダメージは Listner で処理する
                    fireballs.put(fireball.getUniqueId(), new MagicFireball(fireball, player, hitDamage));

                    // Sound;
                    world.playSound(playerLoc, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

                    // FIXME: ターゲットブロックにvfxが表示されない
                    vfx.playEffect(targetLocation.add(new Vector(0, 1, 0)), 5);

                    // Fireball を上に
//                    spawnRelativeLoc.add(new Vector(0, 1.5, 0));

                    return true;
                })
                .repeat(TickTime.TICK, shootIntervalInTick, 5, shootCount)
                .run();

        return true;
    }

    @EventHandler
    private void onFireballHitEntity(ProjectileHitEvent event) {
        final Projectile projectile = event.getEntity();
        if (!(projectile instanceof Fireball fireball)) return;

        if (!fireball.hasMetadata(FIREBALL_METADATA)) return;

        MagicFireball magicFireball = fireballs.get(fireball.getUniqueId());
        fireballs.remove(fireball.getUniqueId());
        final Entity hitEntity = event.getHitEntity();

        if (!(hitEntity instanceof LivingEntity target)) return;

        target.damage(magicFireball.damage, magicFireball.shooter);

        // 直撃ダメージはプレイヤーにサウンド
        magicFireball.shooter.playSound(
                magicFireball.shooter.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Fireball fireball)) return;

        if (!fireball.hasMetadata(FIREBALL_METADATA)) return;

        // Fireball のブロック破壊を無効化
        event.blockList().clear();
    }
}
