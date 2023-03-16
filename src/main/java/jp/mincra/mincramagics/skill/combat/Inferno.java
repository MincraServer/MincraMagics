package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.core.MincraPlayer;
import jp.mincra.mincramagics.core.PlayerManager;
import jp.mincra.mincramagics.core.SkillCooldown;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
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
        SkillCooldown cooldown = mPlayer.getCooldown();
        if (cooldown.isCooldown(property.skillId())) {
            return;
        }
        cooldown.setCooldown(property.skillId(), property.cooldown());
        mPlayer.getMp().subMp(property.consumedMp());

        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 4F);

        // Start repeating
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // Spawn fireball
                    Vector eyeDirection = player.getEyeLocation().getDirection().normalize();
                    Location spawnLoc = playerLoc.add(new Vector(0, 5, 0));
                    Fireball fireball = (Fireball) world.spawnEntity(spawnLoc, EntityType.FIREBALL);
                    fireball.setVelocity(eyeDirection
                            .add(new Vector(0, -0.6, 0))
                            .multiply(0.7));

                    // Sound;
                    world.playSound(playerLoc, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
                })
                .repeat(TickTime.TICK, 5, 3, 5)
                .run();
    }
}
