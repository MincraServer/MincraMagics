package jp.mincra.mincramagics.skill.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 追加ダメージ付きで雷を落とすユーティリティクラス
 */
public class StrikeLightning implements Listener {
    public enum Mode {
        EFFECT_ONLY,
        DAMAGE
    }

    private final String METADATA_KEY = "StrikeLightning_LightningImmunity";
    private final JavaPlugin plugin;

    public StrikeLightning(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    public void execute(LivingEntity caster, LivingEntity target, double extraDamage, Mode mode) {
        if (mode == Mode.EFFECT_ONLY) {
            target.getWorld().strikeLightningEffect(target.getLocation());
        } else {
            target.getWorld().strikeLightning(target.getLocation());
        }
        if (!target.equals(caster)) {
            // 自傷ダメージは無効化
            target.damage(extraDamage, caster);
        }
        setLightningMetadata(target);
    }

    public void execute(LivingEntity caster, Location targetLocation, Mode mode) {
        if (mode == Mode.EFFECT_ONLY) {
            targetLocation.getWorld().strikeLightningEffect(targetLocation);
        } else {
            targetLocation.getWorld().strikeLightning(targetLocation);
        }
        setLightningMetadata(caster);
    }

    private void setLightningMetadata(LivingEntity entity) {
        final double currentTick = Bukkit.getServer().getCurrentTick();
        entity.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, currentTick));
        // 3 tick後にメタデータを削除
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // もし currentTick と同じ値なら削除
            final var metas = entity.getMetadata(METADATA_KEY);
            for (var meta : metas) {
                if (meta.getOwningPlugin() == plugin && meta.asDouble() == currentTick) {
                    entity.removeMetadata(METADATA_KEY, plugin);
                    break;
                }
            }
        }, 3L);
    }

    // 自分が召喚した雷に打たれた際のダメージは無効化
    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof org.bukkit.entity.LightningStrike)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        // BASE_METADATA_KEYから始まるメタデータがあればキャンセル
        for (var meta : player.getMetadata(METADATA_KEY)) {
            if (meta.getOwningPlugin() == plugin) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
