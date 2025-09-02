package jp.mincra.mincramagics.skill.job.miner;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.bkvfx.Vfx;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MineralDetection extends MagicSkill {

    // 鉱石の種類とGLOWエフェクトの色をマッピング
    private static final Map<Material, NamedTextColor> ORE_COLORS = new EnumMap<>(Material.class);

    static {
        ORE_COLORS.put(Material.COAL_ORE, NamedTextColor.DARK_GRAY);
        ORE_COLORS.put(Material.DEEPSLATE_COAL_ORE, NamedTextColor.DARK_GRAY);
        ORE_COLORS.put(Material.IRON_ORE, NamedTextColor.GRAY);
        ORE_COLORS.put(Material.DEEPSLATE_IRON_ORE, NamedTextColor.GRAY);
        ORE_COLORS.put(Material.COPPER_ORE, NamedTextColor.GOLD);
        ORE_COLORS.put(Material.DEEPSLATE_COPPER_ORE, NamedTextColor.GOLD);
        ORE_COLORS.put(Material.GOLD_ORE, NamedTextColor.YELLOW);
        ORE_COLORS.put(Material.DEEPSLATE_GOLD_ORE, NamedTextColor.YELLOW);
        ORE_COLORS.put(Material.REDSTONE_ORE, NamedTextColor.RED);
        ORE_COLORS.put(Material.DEEPSLATE_REDSTONE_ORE, NamedTextColor.RED);
        ORE_COLORS.put(Material.EMERALD_ORE, NamedTextColor.GREEN);
        ORE_COLORS.put(Material.DEEPSLATE_EMERALD_ORE, NamedTextColor.GREEN);
        ORE_COLORS.put(Material.LAPIS_ORE, NamedTextColor.BLUE);
        ORE_COLORS.put(Material.DEEPSLATE_LAPIS_ORE, NamedTextColor.BLUE);
        ORE_COLORS.put(Material.DIAMOND_ORE, NamedTextColor.AQUA);
        ORE_COLORS.put(Material.DEEPSLATE_DIAMOND_ORE, NamedTextColor.AQUA);
        // Nether Ores
        ORE_COLORS.put(Material.NETHER_GOLD_ORE, NamedTextColor.YELLOW);
        ORE_COLORS.put(Material.NETHER_QUARTZ_ORE, NamedTextColor.WHITE);
        ORE_COLORS.put(Material.ANCIENT_DEBRIS, NamedTextColor.DARK_PURPLE);
    }

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        // Parameters
        final float level = property.level();
        final float range = 4 + 4 * level;

        Location playerLoc = player.getLocation();
        World world = player.getWorld();

        // Effect and sound
        Vfx vfx = vfxManager.getVfx(Vfx.WAX_ON_PENTAGON);
        if (vfx != null) {
            vfx.playEffect(playerLoc.clone().add(0, 0.5, 0), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));
        }
        world.playSound(playerLoc, Sound.ITEM_SPYGLASS_USE, 1.0f, 0.5f);
        // world.playSound(playerLoc, Sound.EVENT_MOB_EFFECT_TRIAL_OMEN, 1.0f, 1.2f);

        // Core functionality
        final List<Shulker> spawnedShulkers = new ArrayList<>();
        final List<Team> createdTeams = new ArrayList<>();
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Location centerLoc = player.getLocation();
        int searchRadius = (int) Math.ceil(range);
        double rangeSq = range * range;

        // プレイヤーを中心とした球形の範囲を走査
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    Location blockLoc = centerLoc.clone().add(x, y, z);
                    if (blockLoc.distanceSquared(centerLoc) > rangeSq) {
                        continue;
                    }

                    Block block = blockLoc.getBlock();
                    Material blockType = block.getType();

                    // マップに登録された鉱石かチェック
                    if (ORE_COLORS.containsKey(blockType)) {
                        // ブロックの中心にシュルカーをスポーン
                        Location shulkerLoc = block.getLocation().add(0.5, 0, 0.5);

                        world.spawn(shulkerLoc, Shulker.class, shulker -> {
                            // 無敵・無音・AIなし・重力オフに設定
                            shulker.setInvulnerable(true);
                            shulker.setSilent(true);
                            shulker.setAI(false);
                            shulker.setGravity(false);

                            // GLOWエフェクトを付与 (3秒より少し長く設定)
                            shulker.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 65, 0, false, false));
                            shulker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 65, 0, false, false));

                            // 色を適用するためのチームを作成・設定
                            String teamName = "md_" + shulker.getUniqueId().toString().substring(0, 13);
                            Team team = scoreboard.getTeam(teamName);
                            if (team == null) {
                                team = scoreboard.registerNewTeam(teamName);
                            }
                            team.color(ORE_COLORS.get(blockType));
                            team.addEntry(shulker.getUniqueId().toString());

                            spawnedShulkers.add(shulker);
                            createdTeams.add(team);
                        });
                    }
                }
            }
        }

        // スポーンさせたシュルカーとチームを3秒後に削除
        if (!spawnedShulkers.isEmpty()) {
            new BKTween(MincraMagics.getInstance())
                    .execute((v) -> {
                        // シュルカーを削除
                        for (Shulker shulker : spawnedShulkers) {
                            if (shulker.isValid()) {
                                shulker.remove();
                            }
                        }
                        // 後片付けとしてチームを削除
                        for (Team team : createdTeams) {
                            try {
                                team.unregister();
                            } catch (IllegalStateException e) {
                                // 既に登録解除されている場合は何もしない
                            }
                        }
                        return true;
                    })
                    .delay(TickTime.TICK, 60) // 3 sec (60 ticks)
                    .run();
        }

        return true;
    }
}