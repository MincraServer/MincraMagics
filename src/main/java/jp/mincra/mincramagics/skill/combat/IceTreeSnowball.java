package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.utils.FreezeManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IceTreeSnowball extends MagicSkill implements Listener {
    private final FreezeManager freezeManager = FreezeManager.getInstance();
    private final Map<UUID, MagicSnowball> summonedSnowballs = new HashMap<>();
    private record MagicSnowball(Snowball snowball, Player shooter, int duration) {}

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final double level = property.level();
        final double velocityMultiplier = 1.1 + level * 0.3;
        final int duration = (int) (level * 4 * 20); // 4 seconds per level

        // Core functionality
        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        Snowball snowball = world.spawn(player.getEyeLocation(), Snowball.class);
        snowball.setShooter(player);
        snowball.setVelocity(playerLoc.getDirection().multiply(velocityMultiplier));
        // summonedSnowballs でリスナー用に管理
        summonedSnowballs.put(snowball.getUniqueId(), new MagicSnowball(snowball, player, duration));

        // Effect and sound
        player.playSound(playerLoc, Sound.ENTITY_BREEZE_SHOOT, 0.2F, 2F);
        Vfx vfx = vfxManager.getVfx("ice");
        Vector axis = new Vector(0, 1, 0);
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, axis, 0);

        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityHit(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (!(damager instanceof Snowball)) return;

        UUID uuid = damager.getUniqueId();
        if (!summonedSnowballs.containsKey(uuid)) return;

        if (!(e.getEntity() instanceof LivingEntity entity)) return;

        MagicSnowball snowball = summonedSnowballs.get(uuid);

        // if the entity is shooter, ignore the hit
        if (snowball.shooter.equals(entity)) {
            e.setCancelled(true);
            return;
        }

        // ダメージを与える
        freezeManager.freeze(entity, snowball.shooter, snowball.duration);

        // スノーボールを削除
        snowball.snowball.remove();
        summonedSnowballs.remove(uuid);
    }
}
