package jp.mincra.mincramagics.skill.utility;

import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Move extends MagicSkill {

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();

        // Move
        float strength = property.level();
        Vector velocity = playerLoc.getDirection().normalize().multiply(strength).setY(-0.5);
        player.setVelocity(velocity);
        Location targetLoc = playerLoc.clone().add(velocity.multiply(strength * 1).setY(-0.5));

        // Sound
        world.playSound(targetLoc, Sound.ENTITY_WITHER_SHOOT, 1F, 1F);

        // Vfx
        Vector axis = new Vector(0, 1, 0);
        vfxManager.getVfx("move")
                .playEffect(targetLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));

        return true;
    }
}
