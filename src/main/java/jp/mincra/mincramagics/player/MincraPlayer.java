package jp.mincra.mincramagics.player;

import org.bukkit.entity.Player;

public class MincraPlayer {
    private final Player player;
    private final MP mp;
    private final SkillCooldown cooldown;

    public MincraPlayer(Player player, MP mp) {
        this.player = player;
        this.mp = mp;
        this.cooldown = new SkillCooldown();
    }

    public MP getMp() {
        return mp;
    }

    public Player getPlayer() {
        return player;
    }

    public SkillCooldown getCooldown() {
        return cooldown;
    }
}
