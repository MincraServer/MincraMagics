package jp.mincra.mincramagics.skill.utility;

import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Move extends MagicSkill {

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        super.onTrigger(player, property);

        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();

        // Move
        float strength = property.strength();
        Vector velocity = playerLoc.getDirection().normalize().multiply(strength).setY(-0.5);
        player.setVelocity(velocity);
        Location targetLoc = playerLoc.clone().add(velocity.add(new Vector(0, 0.5, 0)).multiply(strength * 2.5));

        // Sound
        world.playSound(targetLoc, Sound.ENTITY_WITHER_SHOOT, 0.1F, 0.1F);
        world.playSound(targetLoc, Sound.ENTITY_BREEZE_SLIDE, 1.15F, 1F);

        // Vfx
        Vector axis = new Vector(0, 1, 0);
        vfxManager.getVfx("move")
                .playEffect(targetLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));
    }
}
