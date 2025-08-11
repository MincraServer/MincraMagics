package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Resistance extends MagicSkill {

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // 視線の先にいるプレイヤーを取得
        Entity target = player.getTargetEntity(3);
        if (!(target instanceof Player targetPlayer)) {
            player.sendMessage("§c視線の先にプレイヤーがいません。");
            return false;
        }

        // Parameters
        int level = (int) property.level();
        int duration = 60 * 20; // 60 seconds in ticks
        int amplifier = level - 1;

        // Core functionality
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, amplifier, false, false, true));

        // Send messages
        player.sendMessage(Component.text(targetPlayer.getName() + "に結界の杖lv1を使用しました").color(NamedTextColor.GREEN));
        targetPlayer.sendMessage(Component.text(player.getName() + "から結界の杖lv1を受けました").color(NamedTextColor.GREEN));

        // Effect and sound
        Location playerLoc = player.getLocation();
        player.playSound(playerLoc, Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1F);
        Vfx vfx = vfxManager.getVfx("instant_effect_pentagon");
        Vector axis = new Vector(0, 1, 0);
        vfx.playEffect(target.getLocation().add(0, 0.5, 0), 5, axis, 0);

        return true;
    }
}
