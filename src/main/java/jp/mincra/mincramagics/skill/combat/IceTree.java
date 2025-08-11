package jp.mincra.mincramagics.skill.combat;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.utils.FreezeManager;
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
        if (!super.onTrigger(player, property)) return false;

        int level = (int) property.level();
        final int radius = level * 5;
        final int durationTick = level * 130;

        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        var monsters = playerLoc.getNearbyLivingEntities(radius, 3, radius, e -> e instanceof Monster);

        Vfx vfx = vfxManager.getVfx("ice");
        Vector axis = new Vector(0, 1, 0);

        for (var monster : monsters) {
            world.playSound(playerLoc, Sound.BLOCK_GLASS_BREAK, 0.5F, 1F);
            vfx.playEffect(monster.getLocation().add(0, 0.5, 0), 5, axis, 0);

            if (freezeManager == null) freezeManager = FreezeManager.getInstance();
            freezeManager.freeze(monster, player, durationTick);
        }


        return true;
    }
}
