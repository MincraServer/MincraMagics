package jp.mincra.mincramagics.oraxen.mechanic.artifact;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MaterialSlot;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbtobject.ArtifactNBT;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class ArtifactMechanicManager implements Listener {
    private final ArtifactMechanicFactory factory;
    private final MaterialManager materialManager;
    private final SkillManager skillManager;
    private final Map<UUID, Integer> disableLeftTrigger;
    private final Server server;

    private record MaterialSkill(MaterialProperty materialProperty, MagicSkill skill) {}

    public ArtifactMechanicManager(ArtifactMechanicFactory factory) {
        this.factory = factory;
        materialManager = MincraMagics.getMaterialManager();
        skillManager = MincraMagics.getSkillManager();
        disableLeftTrigger = new HashMap<>();
        server = Bukkit.getServer();
    }

    @EventHandler
    private void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean isLeft = e.getAction().isLeftClick();

        // Qキーを推した時に左クリックも発動するバグの対策.
        if (isLeft && disableLeftTrigger.containsKey(uuid) && disableLeftTrigger.get(player.getUniqueId()) > server.getCurrentTick()) {
            return;
        }

        boolean triggered = triggerMaterial(e.getPlayer(), item, isLeft ? TriggerType.LEFT : TriggerType.RIGHT);
        e.setCancelled(triggered);
    }

    @EventHandler
    private void onSwap(PlayerSwapHandItemsEvent e) {
        boolean triggered = triggerMaterial(e.getPlayer(), e.getOffHandItem(), TriggerType.SWAP);
        e.setCancelled(triggered);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (e.getPlayer().getOpenInventory().getType() == InventoryType.PLAYER) {
            // プレイヤーがインベントリ開いてるときは無視
            return;
        }
        boolean triggered = triggerMaterial(player, e.getItemDrop().getItemStack(), TriggerType.DROP);
        if (triggered) {
            disableLeftTrigger.put(player.getUniqueId(), server.getCurrentTick() + 2);
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onArmorChanged(PlayerArmorChangeEvent e) {
        equipMaterial(e.getPlayer(), e.getNewItem(), e.getOldItem(), false);
    }

    @EventHandler
    private void onItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        ItemStack oldItem = player.getInventory().getItem(e.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(e.getNewSlot());
        equipMaterial(player, newItem, oldItem, true);
    }

    // Artifact のクラフト時に NBT タグが上書きされないので, このメソッドで上書きする
    // Oraxen の RecipesEventsManager#onCrafted() の priority が HIGHEST なので, その次に実行されるようにする
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onCrafted(PrepareItemCraftEvent event) {
        final ItemStack result = event.getInventory().getResult();
        if (result == null || result.getType().isAir()) return;

        if (!OraxenItems.exists(result)) return;

        final String itemId = OraxenItems.getIdByItem(result);
        if (factory.isNotImplementedIn(itemId)) return;

        final ItemBuilder builder = OraxenItems.getItemById(itemId);
        final ItemStack trueResult = builder.build();
        event.getInventory().setResult(trueResult);
    }

    /**
     * Trigger the material skill based on the trigger type.
     * @param caster The player who triggers the material.
     * @param item The item that contains the material.
     * @param triggerType The trigger type of the material (LEFT, RIGHT, SWAP, DROP, PASSIVE).
     * @return True if the skill was successfully triggered, false otherwise.
     */
    private boolean triggerMaterial(Player caster, ItemStack item, TriggerType triggerType) {
        final List<MaterialSkill> materialSkills = findMaterial(item, triggerType);
        if (materialSkills == null) return false;

        // 一つでもスキルがトリガーされたら true を返す
        return materialSkills.stream().anyMatch(materialSkill -> materialSkill.skill.onTrigger(caster, materialSkill.materialProperty));
    }

    /**
     * Equip the material to the player.
     *
     * @param caster The player who equips the material.
     * @param newItem   The item that contains the material.
     */
    private void equipMaterial(Player caster, ItemStack newItem, ItemStack oldItem, boolean isOnItemHeldEvent) {
        List<MaterialSkill> materialSkills = findMaterial(newItem, TriggerType.PASSIVE);
        List<MaterialSkill> oldMaterialSkills = findMaterial(oldItem, TriggerType.PASSIVE);
        // 新しいアイテムと古いアイテムのマテリアルスキルが同じ場合は何もしない
        if (materialSkills != null && materialSkills.equals(oldMaterialSkills)) return;

        if (materialSkills != null) {
            if (isOnItemHeldEvent && isNotEquippable(newItem.getType())) return;

            for (MaterialSkill materialSkill : materialSkills) {
                materialSkill.skill.onEquip(caster, materialSkill.materialProperty);
            }
        }
        if (oldMaterialSkills != null) {
            if (isOnItemHeldEvent && isNotEquippable(oldItem.getType())) return;

            for (MaterialSkill materialSkill : oldMaterialSkills) {
                materialSkill.skill.onUnequip(caster, materialSkill.materialProperty);
            }
        }
    }

    private boolean isNotEquippable(Material material) {
        return !List.of(
                Material.LEATHER_HELMET,
                Material.CHAINMAIL_HELMET,
                Material.IRON_HELMET,
                Material.GOLDEN_HELMET,
                Material.DIAMOND_HELMET,
                Material.NETHERITE_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.CHAINMAIL_CHESTPLATE,
                Material.IRON_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE,
                Material.DIAMOND_CHESTPLATE,
                Material.NETHERITE_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.CHAINMAIL_LEGGINGS,
                Material.IRON_LEGGINGS,
                Material.GOLDEN_LEGGINGS,
                Material.DIAMOND_LEGGINGS,
                Material.NETHERITE_LEGGINGS,
                Material.LEATHER_BOOTS,
                Material.CHAINMAIL_BOOTS,
                Material.IRON_BOOTS,
                Material.GOLDEN_BOOTS,
                Material.DIAMOND_BOOTS,
                Material.NETHERITE_BOOTS,
                Material.ELYTRA
        ).contains(material);
    }

    @Nullable
    private List<MaterialSkill> findMaterial(ItemStack item, TriggerType triggerType) {
        // Get Oraxen Item
        String itemId = OraxenItems.getIdByItem(item);
        if (factory.isNotImplementedIn(itemId))
            return null;

        ArtifactNBT artifactNBT = ArtifactNBT.fromItem(item);
        if (artifactNBT == null) return null;
        Map<MaterialSlot, String> materials = artifactNBT.getMaterialMap();
        MincraMagics.getPluginLogger().info("materials: " + materials);
        List<MaterialSlot> targetSlots = Arrays.stream(MaterialSlot.values()).filter(slot -> slot.getTriggerType() == triggerType).toList();
        MincraMagics.getPluginLogger().info("targetSlots: " + targetSlots);
        List<String> materialIds = targetSlots.stream()
                .map(materials::get)
                .filter(Objects::nonNull)
                .toList();
        MincraMagics.getPluginLogger().info("materialIds: " + materialIds);
        if (materialIds.isEmpty()) return null;

        List<MaterialSkill> materialProperties = materialIds.stream().filter(materialManager::isRegistered)
                .map(materialManager::getMaterial)
                .map(material -> {
                    // Get the skill for the material
                    String skillId = material.skillId();
                    if (!skillManager.isRegistered(skillId)) {
                        MincraMagics.getPluginLogger().warning("Skill " + skillId + " is not registered for material " + material.materialId() + ". Skipping.");
                        return null;
                    }
                    MagicSkill skill = skillManager.getSkill(skillId);
                    return new MaterialSkill(material, skill);
                })
                .filter(Objects::nonNull)
                .toList();
        MincraMagics.getPluginLogger().info("materialProperties: " + materialProperties);

        if (materialProperties.isEmpty()) return null;
        return materialProperties;
    }
}

