package jp.mincra.mincramagics.skill.passive;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HpBoost extends MagicSkill {
    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(MincraMagics.getInstance(), "hp_boost");

    @Override
    public void onEquip(Player player, MaterialProperty property) {
        super.onEquip(player, property);

        // Parameters
        final double level = property.level();
        final double hpBoost = 5.0f * level; // 2 HP per level

        // Core functionality
        final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) {
            MincraLogger.warn("Player " + player.getName() + " does not have MAX_HEALTH attribute.");
            return;
        }
        if (attribute.getModifier(MODIFIER_KEY) == null) {
            attribute.addModifier(new AttributeModifier(
                    MODIFIER_KEY, hpBoost, AttributeModifier.Operation.ADD_NUMBER
            ));

            // Effect and sound
            Vfx vfx = vfxManager.getVfx("instant_effect_pentagon");
            Location playerLoc = player.getLocation();
            vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
            player.playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1F);
        }
    }

    @Override
    public void onUnequip(Player player, MaterialProperty property) {
        super.onUnequip(player, property);

        final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) {
            MincraLogger.warn("Player " + player.getName() + " does not have MAX_HEALTH attribute.");
            return;
        }

        attribute.removeModifier(MODIFIER_KEY);
    }
}
