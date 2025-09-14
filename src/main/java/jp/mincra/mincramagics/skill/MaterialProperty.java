package jp.mincra.mincramagics.skill;

import com.alessiodp.parties.api.Parties;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.utils.Functions;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record MaterialProperty(String materialId, String skillId, float cooldown, float mp, float level,
                               Map<String, Object> extra, boolean alreadyApplied) {
    public MaterialProperty(String materialId, String skillId, float cooldown, float mp, float level,
                            Map<String, Object> extra) {
        this(materialId, skillId, cooldown, mp, level, extra, false);
    }

    private final static int PARTY_EFFECT_RADIUS = 50;

    public MaterialProperty applyEffect(Player player, @Nullable ItemStack artifact) {
        if (alreadyApplied) {
            MincraLogger.warn("MaterialProperty.applyEffect() is called more than once. This is a bug.");
            return this;
        }

        final var party = Optional.ofNullable(Parties.getApi().getPartyOfPlayer(player.getUniqueId()));
        final var nearbyMembersCount = party.map(p -> (int) p.getMembers().stream()
                .map(Bukkit::getPlayer).filter(Objects::nonNull)
                .filter(memberPlayer -> memberPlayer.getWorld().equals(player.getWorld()))
                .filter(memberPlayer -> memberPlayer.getLocation().distance(player.getLocation()) <= PARTY_EFFECT_RADIUS)
                .filter(memberPlayer -> !memberPlayer.equals(player)) // 自分自身は除外
                .count()).orElse(0);
        final var skillLevel = (nearbyMembersCount + 1) / 2;

        final var efficiencyLevel = Optional.ofNullable(artifact)
                .map(i -> i.getItemMeta().getEnchantLevel(Enchantment.EFFICIENCY))
                .orElse(0);
        // lv1: 94%, lv2: 87%, ..., lv5: 72%
        final float cooldownCoefficient = (float) -Functions.logistic(efficiencyLevel, 1, 0.25, 0) + 1.5f;

        return new MaterialProperty(materialId, skillId, cooldown * cooldownCoefficient, mp, level + nearbyMembersCount, extra, true);
    }
}
