package jp.mincra.skill.combat;

import jp.mincra.MincraMagics;
import jp.mincra.core.MincraPlayer;
import jp.mincra.core.PlayerManager;
import jp.mincra.skill.MagicSkill;
import jp.mincra.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Inferno implements MagicSkill {
    private PlayerManager playerManager;
    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        if (playerManager == null) playerManager = MincraMagics.getPlayerManager();
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        mPlayer.getCooldown().setCooldown(property.materialId(), property.cooldown());
        mPlayer.getMp().subMp(property.consumedMp());

        // Spawn fireball
        Vector eyeDirection = player.getEyeLocation().getDirection().normalize();
        Location playerLoc = player.getLocation();
        Location spawnLoc = playerLoc.add(new Vector(0, 5, 0));
        World world = player.getLocation().getWorld();
        Fireball fireball = (Fireball) world.spawnEntity(spawnLoc, EntityType.FIREBALL);
        fireball.setVelocity(eyeDirection
                .add(new Vector(0, -0.3, 0))
                .multiply(1));

        // Sound;
        world.playSound(playerLoc, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
    }
}
