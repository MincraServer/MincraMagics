package jp.mincra.mincramagics.skill.utility;

import de.tr7zw.nbtapi.NBTEntity;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class Jump extends MagicSkill implements Listener {

    private final List<UUID> castedPlayer = new ArrayList<>();
    private final Map<UUID, Integer> soundInterval = new HashMap<>();

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Jump
        float strength = property.level();
        Vector playerVelocity = player.getVelocity();
        if (playerVelocity.getY() > 0) {
            playerVelocity.add(new Vector(0, 0.4 * strength, 0));
        } else {
            playerVelocity.setY(0.5);
        }
        player.setVelocity(playerVelocity);
        castedPlayer.add(player.getUniqueId());

        // Sound
        Location playerLoc = player.getLocation();
        World world = player.getLocation().getWorld();
        world.playSound(playerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, 1F);

        // Vfx
        vfxManager.getVfx("jump")
                .playEffect(playerLoc, 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));

        return true;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (castedPlayer.contains(uuid)) {
            if (player.isOnGround()) {
                // if on ground
                castedPlayer.remove(uuid);
                player.stopSound(Sound.ITEM_ELYTRA_FLYING);
                soundInterval.remove(uuid);
                return;
            }

            if (player.isSneaking()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 4, 1, false, false, false));

                int currentTick = Bukkit.getServer().getCurrentTick();
                if (!soundInterval.containsKey(uuid) || soundInterval.get(uuid) < currentTick) {
                    player.getWorld().playSound(player, Sound.ITEM_ELYTRA_FLYING, 0.5f, 1);
                    soundInterval.put(uuid, 160 + currentTick);
                }
            }
        }
    }
}
