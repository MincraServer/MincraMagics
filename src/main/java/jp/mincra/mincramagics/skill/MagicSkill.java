package jp.mincra.mincramagics.skill;

import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.player.MP;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.player.SkillCooldown;
import org.bukkit.entity.Player;

public abstract class MagicSkill {
    protected PlayerManager playerManager = MincraMagics.getPlayerManager();
    protected VfxManager vfxManager = MincraMagics.getVfxManager();

    public abstract void onTrigger(Player player, MaterialProperty property);

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
