package jp.mincra.mincramagics.oraxen.mechanic.gui;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.mechanics.Mechanic;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.gui.GUIManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GUIMechanicsManager implements Listener {

    private final GUIMechanicFactory factory;
    private GUIManager guiManager;

    public GUIMechanicsManager(GUIMechanicFactory factory) {
        this.factory = factory;
    }

    @EventHandler
    private void onClickGuiBlock(PlayerInteractEvent event) {
        // 右クリック以外無視
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        String itemId;
        if (OraxenBlocks.isOraxenBlock(block)) {
            Mechanic mechanic = OraxenBlocks.getOraxenBlock(block.getBlockData());
            itemId = mechanic.getItemID();
        } else if (block != null && OraxenFurniture.isFurniture(block)) {
            Mechanic mechanic = OraxenFurniture.getFurnitureMechanic(block);
            itemId = mechanic.getItemID();
        } else {
            return;
        }

        if (factory.isNotImplementedIn(itemId)) return;

        if (guiManager == null)
            guiManager = MincraMagics.getGuiManager();

        GUIMechanic guiMechanic = (GUIMechanic) factory.getMechanic(itemId);
        String guiId = guiMechanic.getGuiId();
        guiManager.open(guiId, event.getPlayer());
    }
}
