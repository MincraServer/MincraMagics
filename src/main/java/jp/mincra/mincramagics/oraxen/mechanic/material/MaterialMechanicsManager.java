package jp.mincra.mincramagics.oraxen.mechanic.material;

import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.skill.MaterialManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CrafterInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
    private void onPrepareCraft(PrepareItemCraftEvent event) {
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

    /**
     * 材料消費とアイテム付与を処理する
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        // クラフトインベントリ以外でのクリックは無視
        if (!(event.getInventory() instanceof CraftingInventory inventory)) {
            return;
        }

        // 結果スロット以外でのクリックは無視
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        // 結果スロットにアイテムがなければ無視
        final var resultItem = event.getCurrentItem();
        if (resultItem == null || resultItem.getType().isAir()) {
            return;
        }

        // onPrepareCraftと同様のロジックで、これがカスタムクラフトであるかを最終判定
        final var ingredients = inventory.getMatrix();
        if (!isCustomLevelUpRecipe(ingredients)) {
            return;
        }

        // --- ここからが重要 ---
        // デフォルトの動作をキャンセルして、すべての処理を手動で行う
        event.setCancelled(true);

        final var player = (Player) event.getWhoClicked();

        // Shiftクリックの処理
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            handleShiftCraft(player, inventory, resultItem);
        }
        // 通常クリックの処理
        else {
            handleNormalCraft(player, inventory, resultItem);
        }
    }

    /**
     * 通常クリック時の処理。カーソルにアイテムをセットし、材料を1回分消費します。
     */
    private void handleNormalCraft(Player player, CraftingInventory inventory, ItemStack resultItem) {
        // プレイヤーのカーソルに既にアイテムがある場合はクラフト不可
        if (!player.getItemOnCursor().getType().isAir()) return;

        player.setItemOnCursor(resultItem);
        consumeIngredients(inventory, 1);
    }

    /**
     * Shiftクリック時の処理。作成可能な最大数を作成し、材料を消費します。
     */
    private void handleShiftCraft(Player player, CraftingInventory inventory, ItemStack resultItem) {
        // 作成可能な最大数を計算
        int maxCraftable = calculateMaxCraftable(inventory);
        if (maxCraftable == 0) return;

        // プレイヤーのインベントリの空きスペースを計算
        int freeSpace = 0;
        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null || content.getType().isAir()) {
                freeSpace += resultItem.getMaxStackSize();
            } else if (content.isSimilar(resultItem)) {
                freeSpace += content.getMaxStackSize() - content.getAmount();
            }
        }

        int amountToCraft = Math.min(maxCraftable, freeSpace / resultItem.getAmount());
        if (amountToCraft == 0) return;

        // アイテムをインベントリに追加し、材料を消費
        ItemStack craftedItems = resultItem.clone();
        craftedItems.setAmount(amountToCraft * resultItem.getAmount());
        player.getInventory().addItem(craftedItems);

        consumeIngredients(inventory, amountToCraft);
    }

    /**
     * 指定された回数分、クラフト材料を消費します。
     */
    private void consumeIngredients(CraftingInventory inventory, int times) {
        ItemStack[] matrix = inventory.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] != null) {
                int newAmount = matrix[i].getAmount() - times;
                if (newAmount > 0) {
                    matrix[i].setAmount(newAmount);
                } else {
                    matrix[i] = null; // アイテムを消す
                }
            }
        }
        inventory.setMatrix(matrix); // 更新した材料をセット
    }

    /**
     * Shiftクリック時に何個までクラフト可能か計算します。
     */
    private int calculateMaxCraftable(CraftingInventory inventory) {
        int maxAmount = Integer.MAX_VALUE;
        for (ItemStack item : inventory.getMatrix()) {
            if (item != null && !item.getType().isAir()) {
                maxAmount = Math.min(maxAmount, item.getAmount());
            }
        }
        return maxAmount == Integer.MAX_VALUE ? 0 : maxAmount;
    }

    /**
     * レシピがカスタムレベルアップレシピであるかを判定するヘルパーメソッド
     */
    private boolean isCustomLevelUpRecipe(ItemStack[] ingredients) {
        final var nullCount = (int) Arrays.stream(ingredients).filter(Objects::isNull).count();
        if ((ingredients.length == 4 && nullCount != 2) || nullCount != 7) return false;

        final var materialItem = Arrays.stream(ingredients).filter(item -> item != null && OraxenItems.exists(item) && materialManager.isRegistered(OraxenItems.getIdByItem(item))).findFirst();
        if (materialItem.isEmpty()) return false;

        final var levelUpItem = Arrays.stream(ingredients).filter(item -> item != null && OraxenItems.exists(item) && LEVEL_UP_INGREDIENT_ID.equals(OraxenItems.getIdByItem(item))).findFirst();
        return levelUpItem.isPresent();
    }
}
