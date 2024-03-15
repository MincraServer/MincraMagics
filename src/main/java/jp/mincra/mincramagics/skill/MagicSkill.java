package jp.mincra.mincramagics.skill;

import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MP;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.player.SkillCooldown;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class MagicSkill {
    protected PlayerManager playerManager = MincraMagics.getPlayerManager();
    protected VfxManager vfxManager = MincraMagics.getVfxManager();

    public boolean onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (!canTrigger(mPlayer, property)) return false;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        // 耐久値
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            if (damageable.hasDamage()) {
                damageable.setDamage(damageable.getDamage() + 1);
                item.setItemMeta(damageable);
                player.getInventory().setItemInMainHand(item);
            }
        }

        return true;
    }

    protected void consumeMp(MincraPlayer player, MaterialProperty property) {
        MP mp = player.getMp();
        mp.subMp(property.mp());
    }

    protected void setCooldown(MincraPlayer player, MaterialProperty property) {
        SkillCooldown cooldown = player.getCooldown();
        cooldown.setCooldown(property.materialId(), property.cooldown());
    }

    protected boolean canTrigger(MincraPlayer player, MaterialProperty property) {
        return player.getMp().isEnoughMP(property.mp())
                && !player.getCooldown().isCooldown(property.materialId());
    }
}
