package jp.mincra.mincramagics.skill.utility;

import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.event.PlayerMpChangedEvent;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// 緑水晶
public class Speeden extends MagicSkill implements Listener {
    private final Set<UUID> disableMpChangePlayers = new HashSet<>();

    @Override
    public boolean onTrigger(Player player, MaterialProperty property) {
        if (!super.onTrigger(player, property)) return false;

        int strength = (int) property.strength();

        int duration = 300 * strength;
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(duration, 15 * strength));

        vfxManager.getVfx("move")
                .playEffect(player.getLocation(), 5, new Vector(0, 1, 0), Math.toRadians(player.getEyeLocation().getYaw()));

        // 所持MPを全消費し, スキルの効果時間が切れたら元に戻る
        UUID uuid = player.getUniqueId();
        MincraPlayer mPlayer = playerManager.getPlayer(uuid);
        float currentMp = mPlayer.getMp();
        mPlayer.setMp(0);
        disableMpChangePlayers.add(uuid);

        new BKTween(MincraMagics.getInstance())
                .execute(v -> {
                    mPlayer.setMp(currentMp);
                    return true;
                })
                .delay(TickTime.TICK, duration)
                .run();

        return true;
    }

    @EventHandler
    private void onMPChanged(PlayerMpChangedEvent event) {
        // スキル発動中はMP回復できない
        if (disableMpChangePlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
