package jp.mincra.mincramagics.player;

import jp.mincra.mincramagics.event.PlayerMpChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;

public class MincraPlayer {
    private final Player player;
    private final MP mpObject;
    private final SkillCooldown cooldown;
    private final Map<MincraAttribute, MincraAttributeInstance> attributes;

    public MincraPlayer(Player player, MP mp) {
        this.player = player;
        this.mpObject = mp;
        this.cooldown = new SkillCooldown();
        final MincraAttributeInstance manaAttribute = new MincraAttributeInstance();
        this.attributes = Map.of(
                MincraAttribute.MAX_MANA, manaAttribute
        );
        mpObject.setMaxManaAttribute(manaAttribute);
    }

    public float getMp() {
        return mpObject.getMp();
    }

    public float getMaxMp() {
        return mpObject.getMaxMp();
    }

    public float getOldMp() {
        return mpObject.getOldMp();
    }

    public boolean isEnoughMP(float requiredMp) {
        return mpObject.isEnoughMP(requiredMp);
    }

    public void setMp(float mp) {
        setMp(mp, false);
    }

    public void setMp(float mp, boolean ignoreMax) {
        mpObject.setMp(mp, ignoreMax);
        Bukkit.getPluginManager().callEvent(new PlayerMpChangedEvent(player, mpObject));
    }

    public void setMaxMp(float maxMp) {
        mpObject.setBaseMaxMp(maxMp);
    }

    public void addMp(float mp, boolean ignoreMax) {
        mpObject.addMp(mp, ignoreMax);
        Bukkit.getPluginManager().callEvent(new PlayerMpChangedEvent(player, mpObject));
    }

    public void subMp(float mp) {
        mpObject.subMp(mp);
        Bukkit.getPluginManager().callEvent(new PlayerMpChangedEvent(player, mpObject));
    }

    public Player getPlayer() {
        return player;
    }

    public SkillCooldown getCooldown() {
        return cooldown;
    }

    @Nullable
    public MincraAttributeInstance getAttribute(MincraAttribute attribute) {
        return attributes.get(attribute);
    }
}
