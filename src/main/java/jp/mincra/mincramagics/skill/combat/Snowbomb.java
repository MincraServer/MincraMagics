package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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

public class Snowbomb extends MagicSkill implements Listener {
    private final Map<UUID, MagicSnowball> summonedSnowballs = new HashMap<>();
    private record MagicSnowball(Snowball snowball, double damage) {}

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        super.onTrigger(player, property);

        Location playerLoc = player.getLocation();

        // Play Vfx
        Location vfxLoc = playerLoc.clone().add(new Vector(0, 0.5, 0));
        Vector axis = new Vector(0, 1, 0);
        Vfx vfx = vfxManager.getVfx("snowbomb");
        vfx.playEffect(vfxLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));

        World world = player.getWorld();

        // Play Sound
        world.playSound(playerLoc, Sound.ENTITY_SNOWBALL_THROW, 1F, 1.15F);
        world.playSound(playerLoc, Sound.ENTITY_BREEZE_SHOOT, 0.15F, 1.05F);

        // Summon Snowball
        Snowball snowball = world.spawn(player.getEyeLocation(), Snowball.class);
        snowball.setShooter(player);
        snowball.setVelocity(playerLoc.getDirection().multiply(1.5));
        // リスナー用にMapに入れる
        summonedSnowballs.put(snowball.getUniqueId(), new MagicSnowball(snowball, property.strength() * 3));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityHit(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (!(damager instanceof Snowball)) return;

        UUID uuid = damager.getUniqueId();
        if (!summonedSnowballs.containsKey(uuid)) return;

        // ダメージを与える
        MagicSnowball snowball = summonedSnowballs.get(uuid);
        e.setDamage(snowball.damage);
    }
}
