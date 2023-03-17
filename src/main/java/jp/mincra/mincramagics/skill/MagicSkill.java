package jp.mincra.mincramagics.skill;

import jp.mincra.mincramagics.core.MP;
import jp.mincra.mincramagics.core.MincraPlayer;
import jp.mincra.mincramagics.core.SkillCooldown;
import org.bukkit.entity.Player;

public abstract class MagicSkill {
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
