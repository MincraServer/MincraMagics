package jp.mincra.mincramagics.skill.combat;

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

public class Resistance extends MagicSkill {

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        // Parameters
        int level = (int) property.level();
        int radius = level * 3; // 3 blocks per level
        int duration = 60 * 20; // 60 seconds in ticks
        int amplifier = level - 1;

        // Core functionality
        // 周囲のプレイヤーを取得
        final var targets = player.getLocation().getNearbyLivingEntities(radius, 3, radius, e -> e instanceof Player).stream()
                .map(Player.class::cast)
                .filter(target -> !target.equals(player)) // 自分自身を除外
                .toList();
        if (targets.isEmpty()) {
            player.sendMessage(Component.text("近くにプレイヤーがいません").color(NamedTextColor.RED));
            return false;
        }

        if (!super.onTrigger(player, property)) return false;

        Vfx vfx = vfxManager.getVfx("instant_effect_pentagon");
        Component itemName = player.getInventory().getItemInMainHand().getItemMeta().itemName();
        // Add Resistance effect
        for (Player target : targets) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, amplifier, false, false, true));
            // Send messages
            player.sendMessage(Component.text(target.getName() + "に").color(NamedTextColor.GREEN).append(itemName).append(Component.text("を使用しました").color(NamedTextColor.GREEN)));
            target.sendMessage(Component.text(player.getName() + "から").color(NamedTextColor.GREEN).append(itemName).append(Component.text("を受けました").color(NamedTextColor.GREEN)));
            vfx.playEffect(target.getLocation().add(0, 0.5, 0), 5, new Vector(0, 1, 0), 0);
        }

        // Effect and sound
        Location playerLoc = player.getLocation();
        player.getWorld().playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1F);
        vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));

        return true;
    }
}
