package jp.mincra.mincramagics.skill.utils;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * 凍傷 (Frostbite) クラス
 * 凍傷効果を与えられたエンティティは, その時間に応じて異なる効果を受ける.
 * - 3秒未満: 移動速度低下
 * - 3秒以上: 凍結 (NoAI + block_display による視覚効果 + 継続ダメージ)
 * また, 凍傷効果が 5 秒間付与されなかった場合, 凍傷効果は解除される.
 */
public class Frostbite implements Listener {
    private final static String META_INIT_TICk = "Frostbite_InitTick";
    private final static String META_LAST_CASTED_TICk = "Frostbite_CurrentTick";
    private final static String META_STATUS = "Frostbite_Status";
    private final static String META_INITIAL_AI = "Frostbite_DefaultAI";

    private final JavaPlugin plugin;
    private final int slownessLevel;
    private final double damagePerSecond;
    private final List<BlockDisplay> iceBlocks = new ArrayList<>();

    private enum Status {
        NONE,
        SLOWNESS,
        FROZEN
    }

    public Frostbite(JavaPlugin plugin, int slownessLevel, double damagePerSecond) {
        this.plugin = plugin;
        this.slownessLevel = slownessLevel;
        this.damagePerSecond = damagePerSecond;
    }


    public void execute(LivingEntity caster, LivingEntity target) {
        final var initTick = target.getMetadata(META_INIT_TICk).stream()
                .filter(meta -> meta.getOwningPlugin() == plugin)
                .mapToDouble(MetadataValue::asDouble)
                .max()
                .orElse(-1);
        final var currentTick = Bukkit.getServer().getCurrentTick();
        final var currentMode = target.getMetadata(META_STATUS).stream()
                .filter(meta -> meta.getOwningPlugin() == plugin)
                .map(meta -> meta.value() instanceof Status status ? status : Status.NONE)
                .findFirst()
                .orElse(Status.NONE);

        if (initTick == -1) {
            target.setMetadata(META_INIT_TICk, new FixedMetadataValue(plugin, currentTick));
            target.setMetadata(META_INITIAL_AI, new FixedMetadataValue(plugin, target.hasAI()));
        }

        MincraLogger.debug(String.format("Frostbite: initTick=%.0f, currentTick=%d, currentMode=%s",
                initTick, currentTick, currentMode.name()));

        // 3 秒未満であれば移動速度低下
        if (initTick == -1 || currentTick - initTick < 60) {
            target.setMetadata(META_STATUS, new FixedMetadataValue(plugin, Status.SLOWNESS));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, slownessLevel, true, true, true));
        } else if (currentMode == Status.SLOWNESS) {
            // 3 秒以上経過していて, 現在移動速度低下状態であれば, 凍結状態に移行
            target.setMetadata(META_STATUS, new FixedMetadataValue(plugin, Status.FROZEN));

            target.setAI(false);
            final var knockAtt = target.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
            if (knockAtt != null) {
                knockAtt.setBaseValue(1.0); // ノックバック無効化
            } else {
                target.registerAttribute(Attribute.KNOCKBACK_RESISTANCE);
                target.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
            }
            spawnIceDisplay(target);
            target.getLocation().getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5F, 1F);

            // 1秒ごとに凍結効果を与える
            new BKTween(plugin)
                    .execute(v -> {
                        if (target.isDead() || !target.isValid()) return false;
                        // メタデータが消えていたら終了
                        final var metas = target.getMetadata(META_STATUS);
                        if (metas.stream().noneMatch(meta -> meta.getOwningPlugin() == plugin && meta.asString().equals(Status.FROZEN.name()))) {
                            return false;
                        }

                        target.damage(damagePerSecond / 2, caster);
                        return true;
                    })
                    .repeat(TickTime.TICK, 10, 0, -1)
                    .run();
        }

        target.setMetadata(META_LAST_CASTED_TICk, new FixedMetadataValue(plugin, currentTick));

        new BKTween(plugin)
                .execute((v) -> {
                    if (target.isDead() || !target.isValid()) {
                        iceBlocks.forEach(Entity::remove);
                        return false;
                    }

                    // 5 秒後にメタデータを削除
                    final var metas = target.getMetadata(META_LAST_CASTED_TICk);
                    for (var meta : metas) {
                        if (meta.getOwningPlugin() == plugin && currentTick - meta.asDouble() >= -1) {
                            MincraLogger.debug("Frostbite: currentTick: " + currentTick + ", lastCastedTick: " + meta.asDouble() + ", removing frostbite effect");
                            final var initialAI = target.getMetadata(META_INITIAL_AI).stream()
                                    .filter(m -> m.getOwningPlugin() == plugin)
                                    .map(MetadataValue::asBoolean)
                                    .findFirst()
                                    .orElse(true);
                            target.setAI(initialAI);
                            target.removePotionEffect(PotionEffectType.SLOWNESS);
                            final var knockAtt = target.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
                            if (knockAtt != null) {
                                knockAtt.setBaseValue(0.0); // ノックバック元に戻す
                            }

                            target.removeMetadata(META_LAST_CASTED_TICk, plugin);
                            target.removeMetadata(META_INIT_TICk, plugin);
                            target.removeMetadata(META_STATUS, plugin);
                            target.removeMetadata(META_INITIAL_AI, plugin);

                            target.getPassengers()
                                    .stream().filter(e -> e instanceof BlockDisplay block && block.getBlock().getMaterial().equals(Material.ICE))
                                    .forEach(Entity::remove);

                            break;
                        }
                    }

                    return null;
                })
                .delay(TickTime.SECOND, 5)
                .run();

    }

    private void spawnIceDisplay(Entity target) {
        // ice の block_display をいくつか重ねて視覚効果を与える
        final int blockAmount = (int) (3 * target.getHeight());
        for (int i = 0; i < blockAmount; i++) {
            final var yOffset = 0.5 + target.getHeight() / (1.25 * blockAmount) * i;
            target.getLocation().getWorld().spawn(target.getLocation().add(0, yOffset - 0.5, 0), BlockDisplay.class, (display) -> {
                // ランダムに回転
                display.setBlock(Material.ICE.createBlockData());
                display.setInterpolationDuration(0);
                display.setInterpolationDelay(0);
                iceBlocks.add(display);

                final Vector3f center = new Vector3f(0.5f, 0.5f, 0.5f);
                // 2. 適用したい回転を定義 (ここではランダムな回転)
                final Quaternionf rotation = new Quaternionf().rotateXYZ(
                        (float) (Math.random() * Math.PI * 2),
                        (float) (Math.random() * Math.PI * 2),
                        (float) (Math.random() * Math.PI * 2)
                );
                // 3. 回転によって中心点がどこへ移動するかを計算
                Vector3f rotatedCenter = new Vector3f(center);
                rotation.transform(rotatedCenter); // `rotatedCenter`が回転後の座標に更新される

                // 4. 元の中心点から、回転後の中心点を引いて、ズレを補正するための移動量を算出
                //    (例: 中心点が+0.2動いたなら、-0.2移動させて元に戻す)
                Vector3f translation = new Vector3f(center).sub(rotatedCenter.mul(0.8f)).sub(0, 1.5f, 0);

                display.setTransformation(new Transformation(
                        translation,          // 移動 (Translation)
                        rotation,             // 左回転 (Left Rotation)
                        new Vector3f(1f, 1f, 1f), // 拡大・縮小 (Scale)
                        new Quaternionf()     // 右回転 (Right Rotation) - 通常は不要
                ));

                target.addPassenger(display);
            });
        }
    }
}
