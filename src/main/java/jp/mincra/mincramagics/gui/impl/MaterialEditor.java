package jp.mincra.mincramagics.gui.impl;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.mincramagics.gui.InventoryGUI;
import jp.mincra.mincramagics.nbtobject.MagicStaffNBT;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.titleupdater.InventoryUpdate;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MaterialEditor extends InventoryGUI {
    private final static List<String> slotIdList = Arrays.asList("left", "right", "swap", "drop");
    // region oraxen glyph
    private static final String NEG_128 = "%oraxen_neg_shift_128%";
    private static final String NEG_64 = "%oraxen_neg_shift_64%";
    private static final String NEG_8 = "%oraxen_neg_shift_8%";
    private static final String GUI_ACTIVATED = "%oraxen_gui_material_editor_activated%";
    private static final String GUI_INACTIVATED = "%oraxen_gui_material_editor_inactivated%";
    // endregion

    private final Inventory inv;
    private final MaterialManager materialManager;
    private final JavaPlugin mincramagics;
    private Player player;
    private final ItemStack invisibleItem;

    private final static String activeTitle =
            Color.COLOR_WHITE +
            PlaceholderAPI.setPlaceholders(null, NEG_8 + GUI_ACTIVATED)
            + Color.COLOR_DARK_GRAY
            + PlaceholderAPI.setPlaceholders(null, NEG_64 + NEG_128 + "マテリアル作業台");
    private final static String inactiveTitle = Color.COLOR_WHITE +
            PlaceholderAPI.setPlaceholders(null, NEG_8 + GUI_INACTIVATED)
            + Color.COLOR_DARK_GRAY
            + PlaceholderAPI.setPlaceholders(null, NEG_64 + NEG_128 + "マテリアル作業台");

    public MaterialEditor() {
        inv = Bukkit.createInventory(null, 27, Component.text(inactiveTitle));

        invisibleItem = OraxenItems.getItemById("invisible_item").build();
        for (int i = 0; i < 27; i++) {
            if (!isAvailableSlot(i)) {
                inv.setItem(i, invisibleItem);
            }
        }
        materialManager = MincraMagics.getMaterialManager();
        mincramagics = MincraMagics.getInstance();
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        // MaterialEditorのインベントリかどうか
        if (!event.getInventory().equals(getInventory())) return;
        int slot = event.getRawSlot();
        InventoryAction action = event.getAction();
        ItemStack currentItem = event.getCurrentItem();

        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (isTransparentSlot(slot) || (currentItem != null && currentItem.equals(invisibleItem))) {
                event.setCancelled(true);
                return;
            }

            if (!isMaterialEditorSlot(event.getRawSlot())) {
                // プレイヤーインベントリ -> Material作業台
                int topLeftSlot = getTopLeftEmptySlot();
                if (isMaterialEditorSlot(topLeftSlot)) {
                    boolean success = onPlace(topLeftSlot, currentItem);
                    event.setCancelled(!success);
                }
            } else {
                //  Material作業台 -> プレイヤーインベントリ
                onPickup(slot);
            }
        } else {
            // プレイヤーのインベントリのとき
            if (slot > 26) {
                return;
            }

            // 操作可能なスロットじゃないとき
            if (!isAvailableSlot(slot)) {
                event.setCancelled(true);
                return;
            }

            // Main Code
            ItemStack cursorItem = event.getCursor();

            if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE) {
                event.setCancelled(!onPlace(slot, cursorItem));
            } else if (action == InventoryAction.PICKUP_ALL) {
                onPickup(slot);
            } else if (action == InventoryAction.SWAP_WITH_CURSOR && !isMagicStaffSlot(slot)) {
                // マテリアルの入れ替えしかできない(magicstuff入れ替えは実装がめんどくさい)
                onPickup(slot);
                event.setCancelled(!onPlace(slot, cursorItem));
            } else {
                event.setCancelled(true);
            }
        }


    }

    @EventHandler
    private void onDrag(InventoryDragEvent event) {
        // MaterialEditorのインベントリかどうか
        if (!event.getInventory().equals(getInventory())) return;

        if (event.getRawSlots().stream().allMatch(this::isMaterialEditorSlot)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        // MaterialEditorのインベントリかどうか
        if (!event.getInventory().equals(getInventory())) return;

        ItemStack magicStuff = getMagicStaff();
        // 魔法武器がまだスロットに入ってたら
        if (magicStuff != null) {
            if (isFullInventory(player)) {
                // インベントリがいっぱいなら
                Location playerLoc = player.getLocation();
                playerLoc.getWorld().dropItem(playerLoc, magicStuff);
            } else {
                player.getInventory().addItem(magicStuff);
            }
        }
    }

    /**
     *
     * @param slot 置かれたスロット
     * @param placedItem 置かれたアイテム
     * @return 適切なアイテムが置かれたらtrue
     */
    private boolean onPlace(int slot, ItemStack placedItem) {
        if (isMagicStaffSlot(slot)) {
            if (placedItem == null) {
                return false;
            }

            // 魔法武器スロットに置いたとき
            MagicStaffNBT magicStaffNBT = MagicStaffNBT.getMincraNBT(placedItem);
            if (magicStaffNBT == null) return false;

            Map<String, String> materialMap = magicStaffNBT.getMaterialMap();

            for (int i = 0; i < 4; i++) {
                String slotId = slotIdList.get(i);
                if (materialMap.containsKey(slotId)) {
                    String materialId = materialMap.get(slotId);
                    if (OraxenItems.exists(materialId)) {
                        ItemStack material = OraxenItems.getItemById(materialId).build();
                        inv.setItem(12 + i, material);
                    }
                }
            }
            changeTitle(activeTitle);
            return true;
        } else {
            // マテリアルのとき
            return registerMaterial(placedItem, slot);
        }
    }

    private void onPickup(int slot) {
        if (isMagicStaffSlot(slot)) {
            // マテリアルを削除
            for (int i = 12; i < 16; i++) {
                inv.setItem(i, null);
            }
            changeTitle(inactiveTitle);
        } else {
            // マテリアルを取ったとき
            registerMaterial(null, slot);
        }
    }

    @Override
    protected void open(Player player) {
        player.openInventory(inv);
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    private boolean isAvailableSlot(int slot) {
        return slot > 26 || slot == 10 || (slot > 11 && slot < 16);
    }

    private boolean isMaterialEditorSlot(int slot) {
        return slot < 27;
    }

    private boolean isTransparentSlot(int slot) {
        return (slot > 0 && slot < 10) || slot == 11 || (slot > 15 && slot < 27);
    }

    private ItemStack getMagicStaff() {
        return inv.getItem(10);
    }

    private void setMagicStaff(ItemStack item) {
        inv.setItem(10, item);
    }

    private boolean isMagicStaffSlot(int slot) {
        return slot == 10;
    }

    /**
     *
     * @param materialItem
     * @param slot
     * @return 成功したらtrue
     */
    private boolean registerMaterial(ItemStack materialItem, int slot) {
        // マテリアルを取ったとき
        // 魔法武器が置いてなかったらキャンセル
        ItemStack magicStuff = getMagicStaff();
        if (magicStuff == null) return false;

        // 魔法武器のNBT入手
        MagicStaffNBT magicStaffNBT = MagicStaffNBT.getMincraNBT(magicStuff);
        if (magicStaffNBT == null) return false;

        if (materialItem != null) {
            String oraxenMaterialItemId = OraxenItems.getIdByItem(materialItem);
            if (!OraxenItems.exists(materialItem)) return false;
            if (!materialManager.isRegistered(oraxenMaterialItemId)) return false;
            magicStaffNBT.setMaterial(slotIdList.get(slot - 12), oraxenMaterialItemId);
        } else {
            magicStaffNBT.removeMaterial(slotIdList.get(slot - 12));
        }

        ItemStack newItem = magicStaffNBT.setNBTTag(new ItemBuilder(magicStuff)).build();
        setMagicStaff(newItem);

        return true;
    }

    private void changeTitle(String title) {
        new BKTween(mincramagics)
                .delay(TickTime.TICK, 1)
                .execute(v -> {
                    // FIXME: タイトルが更新されないバグを修正
                    player.getOpenInventory().setTitle(title);
//                    final Component component = player.getOpenInventory().title().replaceText(TextReplacementConfig.builder().replacement("/[^_]/" + title + "/g").build());
//                    player.updateInventory();
                    InventoryUpdate.updateInventory(player, title);
                    return true;
                })
                .run();
    }

    private boolean isFullInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }

    private int getTopLeftEmptySlot() {
        ItemStack[] items = inv.getContents();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null || item.getType() == Material.AIR) {
                return i;
            }
        }
        return 999;
    }
}
