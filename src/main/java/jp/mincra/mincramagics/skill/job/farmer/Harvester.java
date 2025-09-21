package jp.mincra.mincramagics.skill.job.farmer;

import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Harvester extends MagicSkill implements Listener {

    // スキルが有効なプレイヤーを管理
    private final Map<UUID, MaterialProperty> activeHarvesters = new ConcurrentHashMap<>();

    // 作物のマテリアルリスト(一旦バニラのみ)
    private static final Set<Material> CROPS_MATERIALS = EnumSet.of(
            Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.POISONOUS_POTATO, Material.BEETROOTS,
            Material.NETHER_WART, Material.SWEET_BERRY_BUSH, Material.COCOA,
            Material.MELON_STEM, Material.PUMPKIN_STEM
    );

    // 作物と種の対応表(一旦バニラのみ)
    private static final Map<Material, Material> CROP_SEED = new EnumMap<>(Material.class);

    static {
            CROP_SEED.put(Material.WHEAT, Material.WHEAT_SEEDS);
            CROP_SEED.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
            CROP_SEED.put(Material.MELON_STEM, Material.MELON_SEEDS);
            CROP_SEED.put(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
            CROP_SEED.put(Material.NETHER_WART, Material.NETHER_WART);
            CROP_SEED.put(Material.CARROTS, Material.CARROT);
            CROP_SEED.put(Material.POTATOES, Material.POTATO);
            CROP_SEED.put(Material.COCOA, Material.COCOA_BEANS);
            CROP_SEED.put(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES);
    }

    @Override
    public void onEquip(Player player, MaterialProperty property) {
        super.onEquip(player, property);
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1, 0.8f);
        activeHarvesters.put(player.getUniqueId(), property);
    }

    @Override
    public void onUnequip(Player player, MaterialProperty property) {
        super.onUnequip(player, property);
        activeHarvesters.remove(player.getUniqueId());
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        MincraLogger.debug("BlockBreakEvent triggered by " + player.getName() + " at " + block.getLocation() + " on block " + block.getType());

        // スキルが有効なプレイヤーか、対象ブロックは作物か、既に処理中のブロックではないかを確認
        if (!activeHarvesters.containsKey(player.getUniqueId())
                || !CROPS_MATERIALS.contains(block.getType())) {
            return;
        }

        MincraLogger.debug("Harvester skill activated for " + player.getName() + " on block " + block.getType());

        MaterialProperty property = activeHarvesters.get(player.getUniqueId());

        // Parameters
        final float level = property.level();
        final int extraSeedCount = (int) (1 + 0.75D * Math.sqrt(level));

        // Debug info
        MincraLogger.debug("Harvester skill extraSeedCount " + extraSeedCount + " , skillLevel " + level);

        // Effect and sound=
        Vfx vfx = vfxManager.getVfx("happy_villager_hexagon");
        if (vfx != null) {
            Location playerLoc = player.getLocation();
            vfx.playEffect(playerLoc.add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_FARMER, 1, 0.8f);

        // 追加で種をドロップ
        ItemStack extraSeeds = new ItemStack(CROP_SEED.get(block.getType()), extraSeedCount);
        block.getWorld().dropItemNaturally(block.getLocation(), extraSeeds);
    }
}
