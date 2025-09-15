package jp.mincra.mincramagics.skill;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbt.ArtifactNBT;
import jp.mincra.mincramagics.nbt.utils.ItemMetas;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.player.SkillCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class MagicSkill {
    protected PlayerManager playerManager = MincraMagics.getPlayerManager();
    protected VfxManager vfxManager = MincraMagics.getVfxManager();

    public boolean onTrigger(Player player, MaterialProperty property) {
        // MP, Cooldown
        MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());
        if (mPlayer == null) {
            player.sendMessage(Component.text("プレイヤー情報が見つかりません。").color(TextColor.fromHexString("#FF0000")));
            return false;
        }
        if (!canTrigger(mPlayer, property)) return false;
        consumeMp(mPlayer, property);
        setCooldown(mPlayer, property);

        // 耐久値
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        // 100% / (L+1) の確率で耐久値を減らす

        if (meta instanceof Damageable damageable && player.getGameMode() != GameMode.CREATIVE) {
            final int unbreakingLevel = meta.getEnchantLevel(Enchantment.UNBREAKING);
            final double breakProb = 1.0d / (unbreakingLevel + 1);

            if (Math.random() < breakProb) {
                if (ItemMetas.getMaxDamage(item) - damageable.getDamage() == 0) {
                    // break the artifact if it has no durability left
                    ArtifactNBT nbt = ArtifactNBT.fromItem(item);
                    if (nbt == null) {
                        return false;
                    }
                    List<ItemStack> materials = nbt.getMaterialMap().values().stream().map(OraxenItems::getItemById).map(ItemBuilder::build).toList();
                    player.give(materials);
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                } else {
                    damageable.setDamage(damageable.getDamage() + 1);
                    item.setItemMeta(damageable);
                }
            }

        }

        return true;
    }

    public void onEquip(Player player, MaterialProperty property) {
        return;
    }

    public void onUnequip(Player player, MaterialProperty property) {
        return;
    }

    protected void consumeMp(MincraPlayer player, MaterialProperty property) {
        player.subMp(property.mp());
    }

    protected void setCooldown(MincraPlayer player, MaterialProperty property) {
        SkillCooldown cooldown = player.getCooldown();
        cooldown.setCooldown(property.materialId(), property.cooldown());
    }

    protected boolean canTrigger(MincraPlayer player, MaterialProperty property) {
        if (!player.isEnoughMP(property.mp())) {
            player.getPlayer().sendMessage(Component.text("MPが足りない！").color(TextColor.fromHexString("#FFFFFF")));
            return false;
        }

        return !player.getCooldown().isCooldown(property.materialId());
    }
}
