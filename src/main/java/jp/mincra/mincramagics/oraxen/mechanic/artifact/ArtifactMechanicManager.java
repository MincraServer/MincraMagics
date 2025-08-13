package jp.mincra.mincramagics.oraxen.mechanic.artifact;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbtobject.ArtifactNBT;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArtifactMechanicManager implements Listener {
    private final ArtifactMechanicFactory factory;
    private final MaterialManager materialManager;
    private final SkillManager skillManager;
    private final Map<UUID, Integer> disableLeftTrigger;
    private final Server server;

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
     *
     * @param item A magic stuff item.
     * @param triggerType スキルをトリガーしたキーまたはマウス
     * @return Successfully triggered or not.
     */
    private boolean triggerMaterial(Player caster, ItemStack item, TriggerType triggerType) {
        // Get Oraxen Item
        String itemId = OraxenItems.getIdByItem(item);
        if (factory.isNotImplementedIn(itemId))
            return false;

        ArtifactNBT artifactNBT = ArtifactNBT.fromItem(item);
        if (artifactNBT == null) return false;
        Map<String, String> materials = artifactNBT.getMaterialMap();
        String materialId = materials.get(triggerType.toString().toLowerCase());
        if (materialId == null) return false;

        if (!materialManager.isRegistered(materialId)) return false;
        MaterialProperty materialProperty = materialManager.getMaterial(materialId);
        String skillId = materialProperty.skillId();
        if (!skillManager.isRegistered(skillId)) return false;
        MagicSkill skill = skillManager.getSkill(skillId);
        return skill.onTrigger(caster, materialProperty);
    }
}

