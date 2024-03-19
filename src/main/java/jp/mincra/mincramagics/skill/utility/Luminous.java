package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Luminous extends MagicSkill {
    private final MincraMagics mincraMagics;


    public Luminous(MincraMagics mincraMagics) {
        this.mincraMagics = mincraMagics;
    }

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        int strength = (int) property.strength();

        final int maxDistance = strength * 20;
        final int speedPerSec = strength * 2;
        final int duration = strength * 15; // 秒
        final int lightLevel = strength * 6;

        Vector direction = player.getLocation().getDirection().multiply((double) speedPerSec / 20);
        AtomicReference<Location> lightLoc = new AtomicReference<>(player.getEyeLocation().add(direction));
        World world = player.getWorld();

        new BKTween(mincraMagics)
                .execute(v -> {
                    Location currentLightLoc = lightLoc.getAndUpdate(l -> l.add(direction));
                    Block block = currentLightLoc.getBlock();
                    Material blockType = block.getType();

                    if (blockType.isAir()) {
                        world.spawnParticle(Particle.WAX_OFF, currentLightLoc, 1, 0.05, 0.05, 0.05, 0);
                        return true;
                    } else {
                        Location prevLightLoc = currentLightLoc.add(direction.multiply(-1));
                        Block prevBlock = prevLightLoc.getBlock();
                        Material prevBlockType = prevBlock.getType();
                        prevBlock.setType(Material.LIGHT);
                        Levelled level = (Levelled) prevBlock.getBlockData();
                        level.setLevel(lightLevel);
                        AtomicInteger levelDecrement = new AtomicInteger(0);

                        // 元のブロックに戻す
                        new BKTween(mincraMagics)
                                // 徐々に弱くなる
                                .execute(w -> {
                                    level.setLevel(levelDecrement.getAndDecrement());
                                    return true;
                                })
                                .repeat(TickTime.SECOND, 1, duration - lightLevel, lightLevel)
                                .execute(w -> {
                                    prevBlock.setType(prevBlockType);
                                    return false;
                                })
                                .delay(TickTime.SECOND, 1)
                                .run();

                        return false;
                    }
                })
                .repeat(TickTime.TICK, 1, 0, maxDistance / speedPerSec * 20)
                .run();

        return true;
    }
}
