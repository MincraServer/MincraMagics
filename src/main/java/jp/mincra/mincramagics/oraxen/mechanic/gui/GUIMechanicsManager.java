package jp.mincra.mincramagics.oraxen.mechanic.gui;

import io.th0rgal.oraxen.api.OraxenBlocks;
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
        if (!OraxenBlocks.isOraxenBlock(block)) return;

        Mechanic mechanic = OraxenBlocks.getOraxenBlock(block.getBlockData());
        String itemId = mechanic.getItemID();

        if (factory.isNotImplementedIn(itemId)) return;

        if (guiManager == null)
            guiManager = MincraMagics.getGuiManager();

        GUIMechanic guiMechanic = (GUIMechanic) factory.getMechanic(itemId);
        String guiId = guiMechanic.getGuiId();
        guiManager.open(guiId, event.getPlayer());
    }
}
