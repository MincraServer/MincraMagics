package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.utils.FreezeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class IceTree extends MagicSkill {
    private FreezeManager freezeManager = null;

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        // Parameters
        int level = (int) property.level();
        final int radius = level * 5;
        final int durationTick = level * 130;

        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        var monsters = playerLoc.getNearbyLivingEntities(radius, 3, radius, e -> e instanceof Monster);

        if (monsters.isEmpty()) {
            player.sendMessage(Component.text("周囲にモンスターがいません。").color(NamedTextColor.RED));
            return false;
        }

        if (!super.onTrigger(player, property)) return false;

        Vfx vfx = vfxManager.getVfx("ice");
        Vector axis = new Vector(0, 1, 0);
        player.playSound(playerLoc, Sound.BLOCK_PORTAL_TRAVEL, 0.2F, 2F);

        for (var monster : monsters) {
            world.playSound(playerLoc, Sound.BLOCK_GLASS_BREAK, 0.5F, 1F);
            vfx.playEffect(monster.getLocation().add(0, 0.5, 0), 5, axis, 0);

            if (freezeManager == null) freezeManager = FreezeManager.getInstance();
            freezeManager.freeze(monster, player, durationTick);
        }


        return true;
    }
}
