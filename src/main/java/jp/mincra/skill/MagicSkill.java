package jp.mincra.skill;

import org.bukkit.entity.Player;

public interface MagicSkill {
    public void onTrigger(Player player, MaterialProperty skillProperty);
}
