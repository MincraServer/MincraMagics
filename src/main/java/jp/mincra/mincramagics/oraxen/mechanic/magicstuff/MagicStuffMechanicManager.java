package jp.mincra.mincramagics.oraxen.mechanic.magicstuff;

import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.nbtobject.MincraNBT;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.SkillManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MagicStuffMechanicManager implements Listener {
    private final MagicStuffMechanicFactory factory;
    private final MaterialManager materialManager;
    private final SkillManager skillManager;

    public MagicStuffMechanicManager(MagicStuffMechanicFactory factory) {
        this.factory = factory;
        materialManager = MincraMagics.getMaterialManager();
        skillManager = MincraMagics.getSkillManager();
    }

    @EventHandler
    private void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean isLeft = e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK;

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
        boolean triggered = triggerMaterial(e.getPlayer(), e.getItemDrop().getItemStack(), TriggerType.DROP);
        e.setCancelled(triggered);
    }

    /**
     *
     * @param item A magic stuff item.
     * @param triggerType
     * @return Successfully triggered or not.
     */
    private boolean triggerMaterial(Player caster, ItemStack item, TriggerType triggerType) {
        // Get Oraxen Item
        String itemId = OraxenItems.getIdByItem(item);
        if (factory.isNotImplementedIn(itemId))
            return false;
        MagicStuffMechanic magicMec = (MagicStuffMechanic) factory.getMechanic(itemId);

        MincraNBT mincraNBT = MincraNBT.getMincraNBT(item);
        if (mincraNBT == null) return false;
        Map<String, String> materials = mincraNBT.getMaterialMap();
        String materialId = materials.get(triggerType.toString().toLowerCase());
        if (materialId == null) return false;

        if (!materialManager.isRegistered(materialId)) return false;
        MaterialProperty materialProperty = materialManager.getMaterial(materialId);
        String skillId = materialProperty.skillId();
        if (!skillManager.isRegistered(skillId)) return false;
        MagicSkill skill = skillManager.getSkill(skillId);
        skill.onTrigger(caster, materialProperty);

        return true;
    }
}

