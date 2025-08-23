package jp.mincra.mincramagics.skill.passive;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraAttribute;
import jp.mincra.mincramagics.player.MincraAttributeInstance;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
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

import java.util.logging.Logger;

public class MpBoost extends MagicSkill {
    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(MincraMagics.getInstance(), "mp_boost");

    private final PlayerManager playerManager = MincraMagics.getPlayerManager();

    @Override
    public void onEquip(Player player, MaterialProperty property) {
        super.onEquip(player, property);

        // Parameters
        final double level = property.level();
        final double hpBoost = 5.0f * level;

        // Core functionality
        final MincraPlayer mincraPlayer = playerManager.getPlayer(player.getUniqueId());
        if (mincraPlayer == null) {
            MincraLogger.warn("Player " + player.getName() + " is not registered in PlayerManager.");
            return;
        }
        final MincraAttributeInstance attribute = mincraPlayer.getAttribute(MincraAttribute.MAX_MANA);
        if (attribute == null) {
            MincraLogger.warn("Player " + player.getName() + " does not have MAX_MANA attribute.");
            return;
        }
        attribute.addModifier(new AttributeModifier(
                MODIFIER_KEY, hpBoost, AttributeModifier.Operation.ADD_NUMBER
        ));

        // Effect and sound
        Vfx vfx = vfxManager.getVfx("mana_charge");
        Location playerLoc = player.getLocation();
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        player.playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1F);
    }

    @Override
    public void onUnequip(Player player, MaterialProperty property) {
        super.onUnequip(player, property);

        final MincraPlayer mincraPlayer = playerManager.getPlayer(player.getUniqueId());
        if (mincraPlayer == null) {
            MincraLogger.warn("Player " + player.getName() + " is not registered in PlayerManager.");
            return;
        }
        final MincraAttributeInstance attribute = mincraPlayer.getAttribute(MincraAttribute.MAX_MANA);
        if (attribute == null) {
            MincraLogger.warn("Player " + player.getName() + " does not have MAX_MANA attribute.");
            return;
        }

        attribute.removeModifier(MODIFIER_KEY);
    }
}
