package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Mine extends MagicSkill {

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        int level = (int) property.level();
        int radius = Math.min(level * 5, 20); // 5 blocks per level, max 20 blocks
        int duration = (120 / Math.max(1 + (level - 1) % 3, 1)) * 20; // 120, 60, 30, 120, 60
        int amplifier = level - 1;
        boolean shouldIncludeSelf = level > 4;

        // Core functionality
        final var targets = player.getLocation().getNearbyLivingEntities(radius, 3, radius, e -> e instanceof Player).stream().map(Player.class::cast)
                .filter(target -> shouldIncludeSelf || !target.equals(player))
                .toList();
        if (targets.isEmpty()) {
            player.sendMessage(Component.text("近くにプレイヤーがいません").color(NamedTextColor.RED));
            return false;
        }

        Vfx vfx = vfxManager.getVfx("happy_villager_hexagon");
        // Add Resistance effect
        Component itemName = player.getInventory().getItemInMainHand().getItemMeta().itemName();
        // Add Resistance effect
        for (Player target : targets) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, amplifier, false, false, true));
            // Send messages
            player.sendMessage(Component.text(target.getName() + "に").color(NamedTextColor.GREEN).append(itemName).append(Component.text("を使用しました").color(NamedTextColor.GREEN)));
            target.sendMessage(Component.text(player.getName() + "から").color(NamedTextColor.GREEN).append(itemName).append(Component.text("を受けました").color(NamedTextColor.GREEN)));
            vfx.playEffect(target.getLocation().add(0, 0.5, 0), 5, new Vector(0, 1, 0), 0);
        }

        // Effect and sound
        Location playerLoc = player.getLocation();
        player.playSound(playerLoc, Sound.BLOCK_ANVIL_USE, 0.4F, 1F);
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));

        return true;
    }
}
