package jp.mincra.mincramagics.skill.job.miner;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Hammer extends MagicSkill implements Listener {

    // スキルが有効なプレイヤーを管理
    private final Map<UUID, MaterialProperty> activeHammers = new ConcurrentHashMap<>();

    // スキルによるループを防ぐためのフラグ
    private final Set<Location> processedByHammer = new HashSet<>();

    // サバイバルで破壊不可能なブロック
    private static final Set<Material> UNBREAKABLE_MATERIALS = EnumSet.of(
            Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
            Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.END_GATEWAY,
            Material.JIGSAW, Material.LIGHT, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID
    );

    // レベルごとの破壊パターンの定義 (相対座標)
    private static final Map<Integer, List<Vector>> DESTRUCTION_PATTERNS = new HashMap<>();

    static {
        // Lv1: 破壊したブロックの真下
        DESTRUCTION_PATTERNS.put(1, List.of(new Vector(0, -1, 0)));

        // Lv2: 破壊したブロックの左右と下
        DESTRUCTION_PATTERNS.put(2, List.of(
                new Vector(1, -1, 0),
                new Vector(0, -1, 0), // 下
                new Vector(-1, -1, 0),
                new Vector(1, 0, 0),  // 右
                new Vector(-1, 0, 0)  // 左
        ));

        // Lv3: 破壊した面を中心に3x3
        List<Vector> pattern3x3 = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue; // 中心ブロックは除く
                pattern3x3.add(new Vector(x, y, 0));
            }
        }
        DESTRUCTION_PATTERNS.put(3, pattern3x3);
    }

    @Override
    public void onEquip(Player player, MaterialProperty property) {
        super.onEquip(player, property);
        activeHammers.put(player.getUniqueId(), property);
    }

    @Override
    public void onUnequip(Player player, MaterialProperty property) {
        super.onUnequip(player, property);
        activeHammers.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block startBlock = event.getBlock();
        Location startLoc = startBlock.getLocation();

        // スキル有効、未処理のブロックかをチェック
        if (!activeHammers.containsKey(player.getUniqueId()) || processedByHammer.contains(startLoc)) {
            return;
        }

        MaterialProperty property = activeHammers.get(player.getUniqueId());
        if (!super.onTrigger(player, property)) return;

        int level = (int) property.level();

        // スキルレベルに対応するパターンがなければ終了
        if (!DESTRUCTION_PATTERNS.containsKey(level)) {
            return;
        }

        // プレイヤーが見ているブロックの面を取得
        BlockFace face = player.getTargetBlockFace(5);
        if (face == null) {
            face = BlockFace.SELF; // 見ている面が取得できなければ何もしない
        }

        // パターンに基づいて破壊対象のブロックリストを取得
        List<Block> blocksToBreak = getBlocksInPattern(startBlock, level, face, player.getLocation(), player.isSneaking());
        if (blocksToBreak.isEmpty()) {
            return;
        }

        // --- 破壊処理の実行 ---
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ItemStack tool = player.getInventory().getItemInMainHand();

        for (Block blockToBreak : blocksToBreak) {
            // --- 各種破壊条件をチェック ---
            // 1. WorldGuard保護チェック
            if (!query.testBuild(BukkitAdapter.adapt(blockToBreak.getLocation()), localPlayer, Flags.BLOCK_BREAK)) {
                continue;
            }
            // 2. 破壊不能ブロックかチェック
            if (UNBREAKABLE_MATERIALS.contains(blockToBreak.getType()) || blockToBreak.getType().getHardness() < 0) {
                continue;
            }
            // 3. ツールで破壊可能かチェック (ドロップアイテムがあるかで簡易的に判定)
            if (blockToBreak.getDrops(tool).isEmpty() && !blockToBreak.isPreferredTool(tool)) {
                continue;
            }

            // Jobsの報酬をシミュレートして付与
            // paymentListener.onBlockBreak(new BlockBreakEvent(blockToBreak, event.getPlayer()));
            Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(blockToBreak, ActionType.BREAK), blockToBreak);

            // --- ブロック破壊と報酬付与 ---
            Location breakLoc = blockToBreak.getLocation();
            processedByHammer.add(breakLoc);
            blockToBreak.breakNaturally(tool);
            processedByHammer.remove(breakLoc);
        }
    }

    /**
     * 指定されたパターンとプレイヤーの向きに基づいて破壊対象のブロックリストを返します。
     */
    private List<Block> getBlocksInPattern(Block origin, int level, BlockFace face, Location playerPos, boolean includeBelow) {
        List<Vector> pattern = DESTRUCTION_PATTERNS.get(level);
        if (pattern == null) {
            return Collections.emptyList();
        }

        // プレイヤーの向きに応じてパターンの向きを決定する基準ベクトル
        Vector up, right;

        switch (face) {
            case NORTH: // -Z
                up = new Vector(0, 1, 0); right = new Vector(-1, 0, 0); break;
            case SOUTH: // +Z
                up = new Vector(0, 1, 0); right = new Vector(1, 0, 0); break;
            case WEST:  // -X
                up = new Vector(0, 1, 0); right = new Vector(0, 0, -1); break;
            case EAST:  // +X
                up = new Vector(0, 1, 0); right = new Vector(0, 0, 1); break;
            case UP:    // +Y
                up = new Vector(0, 0, -1); right = new Vector(1, 0, 0); break;
            case DOWN:  // -Y
                up = new Vector(0, 0, 1); right = new Vector(1, 0, 0); break;
            default: // 真下など、向きが関係ないパターン
                return pattern.stream()
                        .map(offset -> origin.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ()))
                        .collect(Collectors.toList());
        }

        List<Block> blocks = new ArrayList<>();
        Location originLoc = origin.getLocation();

        for (Vector p : pattern) {
            // パターンの相対座標(X,Y)を基準ベクトル(right, up)を使ってワールド座標系に変換
            Vector worldOffset = right.clone().multiply(p.getX()).add(up.clone().multiply(p.getY()));
            // Z座標は常に0として扱う(2Dパターンのため)

            // レベル1のような向きが関係ない絶対座標パターンを考慮
            if (p.lengthSquared() != 0 && worldOffset.lengthSquared() == 0) {
                worldOffset = p;
            }

            Block targetBlock = origin.getWorld().getBlockAt(originLoc.clone().add(worldOffset));
            blocks.add(targetBlock);
        }
        return blocks;
    }
}