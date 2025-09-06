package jp.mincra.mincramagics.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * エンティティの頭上にダメージインジケータを表示・管理するクラス (Passenger方式)。
 */
public final class DamageIndicator implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, ArmorStand> indicatorMap = new HashMap<>();
    private final Map<UUID, ArmorStand> customNameMap = new HashMap<>();
    private final Map<UUID, Entity> parentEntityMap = new HashMap<>(); // Indicator UUID -> Parent Entity
    private final Map<UUID, BukkitTask> cleanupTasks = new HashMap<>();

    public DamageIndicator(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void unregister() {
        this.cleanupTasks.values().forEach(BukkitTask::cancel);
        this.cleanupTasks.clear();
        this.indicatorMap.values().forEach(Entity::remove);
        this.indicatorMap.clear();
    }

    // 他のプラグインによるダメージキャンセルを考慮し、EventPriorityをMONITORに設定
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getEntity() instanceof ArmorStand) {
            return;
        }

//        if (entity.getBossBar() != null) return;

        final boolean playerNearby = entity.getWorld().getPlayers().stream()
                .anyMatch(p -> p.getLocation().distanceSquared(entity.getLocation()) <= 400); // 20*20=400
        if (!playerNearby) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                // runnable実行時にエンティティが無効になっている可能性をチェック
                if (entity.isDead() || !entity.isValid()) {
                    removeIndicator(entity.getUniqueId());
                    return;
                }
                updateIndicator(entity);
            }
        }.runTaskLater(this.plugin, 1L);
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        this.removeIndicator(event.getEntity().getUniqueId());
    }

    private void updateIndicator(final LivingEntity entity) {
        final UUID entityId = entity.getUniqueId();
        var indicator = this.indicatorMap.get(entityId);

        if (indicator == null || !indicator.isValid()) {
            // ★ 変更点: インジケータをエンティティの頭上付近にスポーンさせる
            final Location loc = entity.getLocation().add(0, entity.getHeight(), 0);

            indicator = loc.getWorld().spawn(loc, ArmorStand.class, as -> {
                as.setGravity(false);
                as.setVisible(false);
                as.setInvisible(true);
                as.setCustomNameVisible(true);
                as.setMarker(true);
            });

            // ★ 変更点: エンティティにArmorStandを乗せる
            entity.addPassenger(indicator);
            this.indicatorMap.put(entityId, indicator);
            this.parentEntityMap.put(indicator.getUniqueId(), entity);
        }

        var customName = this.customNameMap.get(entityId);
        if (customName == null || !customName.isValid()) {
            if (entity instanceof Player || entity.customName() != null) {
                final Location loc = entity.getLocation().add(0, entity.getHeight(), 0);
                customName = loc.getWorld().spawn(loc, ArmorStand.class, as -> {
                    as.setGravity(false);
                    as.setVisible(false);
                    as.setInvisible(true);
                    as.setCustomNameVisible(true);
                    as.registerAttribute(Attribute.SCALE);
                    final var scaleAttr = as.getAttribute(Attribute.SCALE);
                    if (scaleAttr != null) {
                        scaleAttr.setBaseValue(0.15); // 非常に小さくする
                    }
                });
                entity.addPassenger(customName);
                this.customNameMap.put(entityId, customName);
                customName.customName(entity instanceof Player player ? player.displayName() : entity.customName());
            }
        }

        indicator.customName(generateHealthBar(entity));

        if (this.cleanupTasks.containsKey(entityId)) {
            this.cleanupTasks.get(entityId).cancel();
        }

        final BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                removeIndicator(entityId);
            }
        }.runTaskLater(this.plugin, 200L); // 10秒 = 200 ticks

        this.cleanupTasks.put(entityId, task);
    }

    private void removeIndicator(final UUID entityId) {
        final ArmorStand indicator = this.indicatorMap.remove(entityId);
        final ArmorStand customName = this.customNameMap.remove(entityId);
        if (indicator != null) {
            indicator.remove();
        }
        if (customName != null) {
            customName.remove();
        }

        final BukkitTask task = this.cleanupTasks.remove(entityId);
        if (task != null) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
    }

    /**
     * ★ 変更点: 元の名前を取得・結合する処理を削除
     * エンティティの体力に基づいてハートのComponentを生成します。
     *
     * @param entity 対象のエンティティ
     * @return 体力バーのComponent
     */
    private Component generateHealthBar(final LivingEntity entity) {
        final var attribute = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) {
            return Component.text("❤".repeat(10), NamedTextColor.RED);
        }
        final double maxHealth = attribute.getValue();
        final double currentHealth = Math.max(0, entity.getHealth());
        final double healthRatio = currentHealth / maxHealth;

        final int redHearts = (int) Math.round(healthRatio * 10);
        final int grayHearts = 10 - redHearts;

        final Component redHeartsComponent = Component.text("❤".repeat(redHearts), NamedTextColor.RED);
        final Component grayHeartsComponent = Component.text("❤".repeat(grayHearts), NamedTextColor.DARK_GRAY);

        return redHeartsComponent.append(grayHeartsComponent);
    }
}