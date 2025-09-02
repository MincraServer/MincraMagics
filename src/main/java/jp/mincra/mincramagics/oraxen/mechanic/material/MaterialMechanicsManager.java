package jp.mincra.mincramagics.oraxen.mechanic.material;

import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MaterialManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.Arrays;
import java.util.Objects;

public class MaterialMechanicsManager implements Listener {
    private final MaterialMechanicFactory factory;
    private final MaterialManager materialManager;

    private static final String LEVEL_UP_INGREDIENT_ID = "celestial_tear";

    public MaterialMechanicsManager(MaterialMechanicFactory factory) {
        this.factory = factory;
        materialManager = MincraMagics.getMaterialManager();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onCrafted(PrepareItemCraftEvent event) {
        // マテリアルをレベルアップ
        final var ingredients = event.getInventory().getMatrix();
        final var nullCount = (int) Arrays.stream(ingredients).filter(Objects::isNull).count();
        MincraLogger.debug("MaterialMechanicsManager: Crafting ingredients: " + Arrays.toString(Arrays.stream(ingredients).map(OraxenItems::getIdByItem).toArray()));
        if ((ingredients.length == 4 && nullCount != 2) || nullCount != 7) return; // マテリアル + 強化素材以外は無視

        final var materialItem = Arrays.stream(ingredients).filter(item -> item != null && OraxenItems.exists(item) && materialManager.isRegistered(OraxenItems.getIdByItem(item))).findFirst();
        if (materialItem.isEmpty()) {
            MincraLogger.debug("MaterialMechanicsManager: No material item found in crafting ingredients.");
            return;
        }

        final var levelUpItem = Arrays.stream(ingredients).filter(item -> item != null && OraxenItems.exists(item) && LEVEL_UP_INGREDIENT_ID.equals(OraxenItems.getIdByItem(item))).findFirst();
        if (levelUpItem.isEmpty()) {
            MincraLogger.debug("MaterialMechanicsManager: No level-up ingredient found in crafting ingredients.");
            return;
        }

        final var materialId = OraxenItems.getIdByItem(materialItem.get());
        // 末尾の _[0-9]+ を削除してマテリアルIDを取得
        final var baseMaterialId = materialId.replaceAll("_[0-9]+$", "");
        // 末尾の _([0-9]+) を取得して現在のレベルを取得
        final var currentLevel = materialId.matches(".*_([0-9]+)$") ? Integer.parseInt(materialId.replaceAll(".*_([0-9]+)$", "$1")) : 0;
        final var nextLevel = currentLevel + 1;
        final var nextMaterialId = baseMaterialId + "_" + nextLevel;

        if (!materialManager.isRegistered(nextMaterialId)) {
            MincraLogger.debug("MaterialMechanicsManager: Next material not registered: " + nextMaterialId);
            return;
        }

        final var resultItemBuilder = OraxenItems.getItemById(nextMaterialId);
        if (resultItemBuilder == null) {
            MincraLogger.debug("MaterialMechanicsManager: Failed to get item by id: " + nextMaterialId);
            return;
        }

        event.getInventory().setResult(resultItemBuilder.build());
    }
}
