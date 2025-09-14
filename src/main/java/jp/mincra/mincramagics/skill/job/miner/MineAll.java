package jp.mincra.mincramagics.skill.job.miner;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MineAll extends MagicSkill implements Listener {

    // スキルが有効なプレイヤーを管理する
    private final Map<UUID, MaterialProperty> activeMiners = new ConcurrentHashMap<>();

    // 鉱石のマテリアルリスト
    private static final Set<Material> ORE_MATERIALS = EnumSet.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS
    );
    private static final Map<Material, Integer> ORE_EXP_AMOUNT = new EnumMap<>(Material.class);

    static {
        ORE_EXP_AMOUNT.put(Material.COAL_ORE, 1);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_COAL_ORE, 1);
        ORE_EXP_AMOUNT.put(Material.IRON_ORE, 1);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_IRON_ORE, 1);
        ORE_EXP_AMOUNT.put(Material.COPPER_ORE, 1);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_COPPER_ORE, 1);
        ORE_EXP_AMOUNT.put(Material.GOLD_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_GOLD_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.REDSTONE_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_REDSTONE_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.EMERALD_ORE, 3);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_EMERALD_ORE, 3);
        ORE_EXP_AMOUNT.put(Material.LAPIS_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_LAPIS_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.DIAMOND_ORE, 3);
        ORE_EXP_AMOUNT.put(Material.DEEPSLATE_DIAMOND_ORE, 3);
        ORE_EXP_AMOUNT.put(Material.NETHER_GOLD_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.NETHER_QUARTZ_ORE, 2);
        ORE_EXP_AMOUNT.put(Material.ANCIENT_DEBRIS, 4);
    }

    // 連鎖破壊による無限ループを防ぐためのフラグ
    private final Set<Location> processedByMineAll = new HashSet<>();

    @Override
    public void onEquip(Player player, MaterialProperty property) {
        super.onEquip(player, property);
        activeMiners.put(player.getUniqueId(), property);
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1, 0.9f);
        MincraLogger.debug("MineAll skill equipped by " + player.getName());
    }

    @Override
    public void onUnequip(Player player, MaterialProperty property) {
        super.onUnequip(player, property);
        activeMiners.remove(player.getUniqueId());
        MincraLogger.debug("MineAll skill unequipped by " + player.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block startBlock = event.getBlock();
        MincraLogger.debug("BlockBreakEvent triggered by " + player.getName() + " at " + startBlock.getLocation());

        // スキルが有効なプレイヤーか、対象ブロックは鉱石か、既に処理中のブロックではないかを確認
        if (!activeMiners.containsKey(player.getUniqueId())
                || !ORE_MATERIALS.contains(startBlock.getType())
                || processedByMineAll.contains(startBlock.getLocation())) {
            return;
        }

        MincraLogger.debug("MineAll skill activated for " + player.getName() + " on block " + startBlock.getType());

        MaterialProperty property = activeMiners.get(player.getUniqueId());
        // onTriggerを呼び出してクールダウン等をチェック
        if (!super.onTrigger(player, property)) return;

        // Parameters
        final float level = property.level();
        final int maxBlockAmount = (int) (5 + level * 5);

        // Effect and sound=
        Vfx vfx = vfxManager.getVfx(Vfx.ENCHANT_PENTAGON);
        if (vfx != null) {
            Location playerLoc = player.getLocation();
            vfx.playEffect(playerLoc.add(0, 1, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_WEAPONSMITH, 1, 0.8f);

        // Core functionality - 連鎖破壊処理
        findAndBreakOres(event, player, startBlock, maxBlockAmount);
    }

    private void findAndBreakOres(BlockBreakEvent event, Player player, Block startBlock, int maxAmount) {
        // --- 探索準備 ---
        Material oreType = startBlock.getType();
        // 探索の起点となるブロックのキュー
        Queue<Block> blocksToCheck = new LinkedList<>();
        // 探索済みのブロックを記録するセット
        Set<Location> visitedLocations = new HashSet<>();
        // 破壊対象のブロックリスト
        List<Block> blocksToBreak = new ArrayList<>();

        blocksToCheck.add(startBlock);
        visitedLocations.add(startBlock.getLocation());

        // --- 幅優先探索(BFS)で隣接する鉱石を見つける ---
        while (!blocksToCheck.isEmpty() && blocksToBreak.size() < maxAmount) {
            Block currentBlock = blocksToCheck.poll();
            blocksToBreak.add(currentBlock);

            // 上下左右前後の6方向をチェック
            for (BlockFace face : BlockFace.values()) {
                if (!face.isCartesian()) continue; // 斜めは除外

                Block neighbor = currentBlock.getRelative(face);
                Location neighborLoc = neighbor.getLocation();

                // 未探索かつ同じ種類の鉱石であれば、次の探索対象に追加
                if (!visitedLocations.contains(neighborLoc) && neighbor.getType() == oreType) {
                    visitedLocations.add(neighborLoc);
                    blocksToCheck.add(neighbor);
                }
            }
        }

        // --- WorldGuardの準備 (ループの外で一度だけ実行) ---
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        // --- ブロックの破壊と報酬の付与 ---
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        ItemStack tool = player.getInventory().getItemInMainHand();

        // 最初のブロックはプレイヤーが破壊したイベントで処理されるため、リストの2番目から処理する
        for (int i = 1; i < blocksToBreak.size(); i++) {
            Block blockToBreak = blocksToBreak.get(i);
            Location loc = blockToBreak.getLocation();

            // 破壊権限がない場合は、このブロックの破壊をスキップ
            if (!query.testBuild(BukkitAdapter.adapt(loc), localPlayer, Flags.BLOCK_BREAK)) continue;

            // Jobsプラグインに破壊アクションを通知
            if (jobsPlayer != null) {
                MincraLogger.debug("Notifying Jobs of block break at " + loc + " for player " + player.getName());
                Jobs.action(jobsPlayer, new BlockActionInfo(blockToBreak, ActionType.BREAK), blockToBreak);
            }

            // このスキルによる破壊であることをマーク
            processedByMineAll.add(loc);
            // アイテムがドロップするように自然破壊
            blockToBreak.breakNaturally(tool);
            blockToBreak.getWorld().spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(ORE_EXP_AMOUNT.get(oreType)));

            // 破壊処理が完了したらマークを解除
            processedByMineAll.remove(loc);
        }
    }
}