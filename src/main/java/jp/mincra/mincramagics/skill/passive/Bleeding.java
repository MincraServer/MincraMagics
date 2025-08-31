package jp.mincra.mincramagics.skill.passive;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Bleeding extends MagicSkill implements Listener {
    private final Logger logger = MincraMagics.getPluginLogger();
    private final Map<UUID, MincraMagics> bleedingInstance = new HashMap<>();
    double healRate = 0;

    @Override
    public void onEquip(Player player, MaterialProperty property) {
        super.onEquip(player, property);

        // Parameters
        final double level = property.level();
        healRate = 1.25D * Math.sqrt(level);

        // Effect and sound
        Vfx vfx = vfxManager.getVfx("happy_villager_hexagon");
        Location playerLoc = player.getLocation();
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        player.playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1F);

        bleedingInstance.put(player.getUniqueId(), MincraMagics.getInstance());
    }

    @Override
    public void onUnequip(Player player, MaterialProperty property) {
        super.onUnequip(player, property);

        if (bleedingInstance.containsKey(player.getUniqueId())) {
            bleedingInstance.remove(player.getUniqueId());
        } else {
            logger.warning("Player " + player.getName() + " does not have hp_recovery metadata.");
        }
    }

    @EventHandler
    private void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) {
            return;
        }
        if (!(e.getEntity() instanceof Monster)) {
            return;
        }

        // Core functionality
        if (!bleedingInstance.containsKey(player.getUniqueId())) {
            return; // Stop if the player has unequipped the skill
        }

        final double damage = e.getFinalDamage();

        // Effect and sound
        Vfx vfx = vfxManager.getVfx("happy_villager_hexagon");
        Location playerLoc = player.getLocation();
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        player.playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1F);

        // heal healRate HP
        double currentHealth = player.getHealth();
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        double healHealth = damage * healRate;
        if (currentHealth < maxHealth) {
            double newHealth = Math.min(currentHealth + healHealth, maxHealth);
            player.setHealth(newHealth);
        }
        return;
    }
}
