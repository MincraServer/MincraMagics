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
        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();

        // Move
        Vector velocity = playerLoc.getDirection().setY(-0.3).normalize().multiply(3);
        player.setVelocity(velocity);
        Location targetLoc = playerLoc.clone().add(velocity.add(new Vector(0, 0.9, 0)).multiply(3));

        // Sound
        world.playSound(targetLoc, Sound.ENTITY_WITHER_SHOOT, 0.2F, 1F);

        // Vfx
        Vector axis = new Vector(0, 1, 0);
        vfxManager.getVfx("move")
                .playEffect(targetLoc, 5, axis, Math.toRadians(player.getEyeLocation().getYaw()));
    }
}
