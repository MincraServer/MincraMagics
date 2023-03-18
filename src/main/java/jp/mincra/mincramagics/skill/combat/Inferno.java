package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Inferno extends MagicSkill {
    private PlayerManager playerManager;
    private VfxManager vfxManager;

    @Override
    public void onTrigger(Player player, MaterialProperty property) {
        if (playerManager == null) playerManager = MincraMagics.getPlayerManager();
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());

        // MP, Cooldown
        if (!canTrigger(mPlayer, property)) {
            return;
        }
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.1F, 4F);

        // Play Vfx
        Location _playerLoc = playerLoc.clone();
        if (vfxManager == null) vfxManager = MincraMagics.getVfxManager();
        vfxManager.getVfx("inferno")
                .playEffect(_playerLoc.add(new Vector(0, 0.5, 0)), 5,
                        new Vector(0, 1, 0), Math.PI * player.getEyeLocation().getYaw() / 180);

        Location spawnLoc = playerLoc.add(new Vector(0, 3, 0));

        // Start repeating
        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    // Spawn fireball
                    Fireball fireball = (Fireball) world.spawnEntity(spawnLoc.add(new Vector(0, 1.5, 0)), EntityType.FIREBALL);
                    // 射程 30 プロパティで変えても良いかも
                    Vector target = player.getTargetBlock(null, 30).getLocation().toVector()
                            // Fireballはプレイヤーの目線より上から飛んでくるので、その分下向きのベクトルを足す。
                            .add(spawnLoc.toVector().clone().multiply(-1)).normalize();
                    Vector velocity = target.multiply(0.9);
                    fireball.setVelocity(velocity);
                    fireball.setShooter(player);

                    // Sound;
                    world.playSound(playerLoc, Sound.ENTITY_BLAZE_SHOOT, 1, 1);
                })
                .repeat(TickTime.TICK, 5, 5, 5)
                .run();
    }
}
