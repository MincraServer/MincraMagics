package jp.mincra.mincramagics.gui.impl;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.constant.Color;
import jp.mincra.mincramagics.gui.InventoryGUI;
import jp.mincra.mincramagics.nbtobject.ArtifactNBT;
import jp.mincra.mincramagics.MaterialSlot;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MaterialEditor extends InventoryGUI {
    // Oraxen Glyphs
    private static final String NEG_128 = "%oraxen_neg_shift_128%";
    private static final String NEG_64 = "%oraxen_neg_shift_64%";
    private static final String NEG_8 = "%oraxen_neg_shift_8%";
    private static final String GUI_ACTIVATED = "%oraxen_gui_material_editor_activated%";
    private static final String GUI_INACTIVATED = "%oraxen_gui_material_editor_inactivated%";

    // Slot Indexes
    private static final int FIRST_SLOT_INDEX = 0;
    private static final int ARTIFACT_SLOT_INDEX = 10;
    private static final int FIRST_MATERIAL_SLOT_INDEX = 12;
    private static final int LAST_MATERIAL_SLOT_INDEX = 16;
    private static final int MATERIAL_EDITOR_SIZE = 27;

    private final MaterialManager materialManager;
    private final Logger logger;
    private final Inventory inv;
    private final JavaPlugin mincramagics;
    private Player player;
    private final ItemStack invisibleItem;
    private final ItemStack unavailableSlotItem;

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
        unavailableSlotItem = OraxenItems.getItemById("unavailable_slot").build();
        materialManager = MincraMagics.getMaterialManager();
        mincramagics = MincraMagics.getInstance();
        logger = MincraMagics.getPluginLogger();
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

    // Event Listeners

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
                    event.setCancelled(!handlePlaceItem(topLeftSlot, currentItem));
                }
            } else {
                //  Material作業台 -> プレイヤーインベントリ
                event.setCancelled(!handlePickupItem(slot));
            }
        } else {
            // プレイヤーのインベントリのとき
            if (slot >= MATERIAL_EDITOR_SIZE) {
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
                event.setCancelled(!handlePlaceItem(slot, cursorItem));
            } else if (action == InventoryAction.PICKUP_ALL) {
                event.setCancelled(!handlePickupItem(slot));
            } else if (action == InventoryAction.SWAP_WITH_CURSOR && !isArtifact(slot)) {
                // マテリアルの入れ替えしかできない(magicstuff入れ替えは実装がめんどくさい)
                event.setCancelled(!handlePickupItem(slot));
                event.setCancelled(!handlePlaceItem(slot, cursorItem));
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

        ItemStack magicStuff = getArtifactItem();
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

    // Event Handlers

    /**
     * アイテムを置いたときの処理
     * @param slot 置かれたスロット
     * @param placedItem 置かれたアイテム
     * @return 適切なアイテムが置かれたらtrue
     */
    private boolean handlePlaceItem(int slot, ItemStack placedItem) {
        if (isArtifact(slot)) {
            return setArtifact(placedItem, slot);
        } else {
            // マテリアルのとき
            if (!isAvailableMaterialSlot(slot)) {
                return false;
            }
            return setMaterial(placedItem, slot);
        }
    }

    /**
     * アイテムを取ったときの処理
     * @param slot 取られたスロット
     */
    private boolean handlePickupItem(int slot) {
        if (isArtifact(slot)) {
            return unsetArtifact(slot);
        } else {
            if (!isAvailableMaterialSlot(slot)) {
                return false; // 利用可能なマテリアルスロットでない場合は何もしない
            }
            return unsetMaterial(slot);
        }
    }

    // Slot Utilities
    private boolean isAvailableMaterialSlot(int slot) {
        if (slot < FIRST_MATERIAL_SLOT_INDEX || slot >= LAST_MATERIAL_SLOT_INDEX) {
            return true; // マテリアルスロットの範囲外
        }

        final ItemStack artifact = getArtifactItem();
        final ArtifactNBT artifactNBT = ArtifactNBT.getMincraNBT(artifact);
        if (artifactNBT == null) {
            return true; // 魔法武器がない場合は未使用スロット
        }
        final MaterialSlot materialSlot = MaterialSlot.fromIndex(slot - FIRST_MATERIAL_SLOT_INDEX);
        if (materialSlot == null) {
            logger.warning("Invalid MaterialSlot index: " + (slot - FIRST_MATERIAL_SLOT_INDEX));
            return true;
        }
        return artifactNBT.isAvailableSlot(materialSlot);
    }

    private boolean isAvailableSlot(int slot) {
        return slot == ARTIFACT_SLOT_INDEX || (FIRST_MATERIAL_SLOT_INDEX <= slot && slot < LAST_MATERIAL_SLOT_INDEX) || MATERIAL_EDITOR_SIZE <= slot;
    }

    private boolean isMaterialEditorSlot(int slot) {
        return slot < MATERIAL_EDITOR_SIZE;
    }

    private boolean isTransparentSlot(int slot) {
        return (FIRST_SLOT_INDEX < slot && slot < ARTIFACT_SLOT_INDEX) || (ARTIFACT_SLOT_INDEX < slot && slot < FIRST_MATERIAL_SLOT_INDEX) || (LAST_MATERIAL_SLOT_INDEX <= slot && slot < MATERIAL_EDITOR_SIZE);
    }

    @Nullable
    private ItemStack getArtifactItem() {
        return inv.getItem(ARTIFACT_SLOT_INDEX);
    }

    private void setArtifact(ItemStack item) {
        inv.setItem(ARTIFACT_SLOT_INDEX, item);
    }

    private boolean isArtifact(int slot) {
        return slot == ARTIFACT_SLOT_INDEX;
    }

    // Item modification methods

    private boolean setArtifact(ItemStack artifactItem, int slot) {
        if (artifactItem == null) {
            return false;
        }

        // 魔法武器スロットに置いたとき
        ArtifactNBT artifactNBT = ArtifactNBT.getMincraNBT(artifactItem);
        if (artifactNBT == null) return false;

        Map<String, String> materialMap = artifactNBT.getMaterialMap();

        for (int i = 0; i < 4; i++) {
            MaterialSlot mSlot = MaterialSlot.fromIndex(i);
            if (mSlot == null) {
                logger.warning("Invalid MaterialSlot index: " + i);
                continue;
            }
            String slotId = mSlot.getSlot();
            if (materialMap.containsKey(slotId)) {
                String materialId = materialMap.get(slotId);
                if (OraxenItems.exists(materialId)) {
                    ItemStack material = OraxenItems.getItemById(materialId).build();
                    inv.setItem(FIRST_MATERIAL_SLOT_INDEX + i, material);
                }
            }
        }

        // 利用可能でないスロットに barrier を置く
        final MaterialSlot[] allSlots = MaterialSlot.values();
        final List<MaterialSlot> availableSlots = artifactNBT.availableSlots();
        final List<MaterialSlot> unavailableSlots = Arrays.stream(allSlots)
                .filter(materialSlot -> !availableSlots.contains(materialSlot))
                .toList();
        for (MaterialSlot mSlot : unavailableSlots) {
            int index = MaterialSlot.indexOf(mSlot);
            if (index != -1) {
                inv.setItem(FIRST_MATERIAL_SLOT_INDEX + index, unavailableSlotItem);
            }
        }

        changeTitle(activeTitle);
        return true;
    }

    private boolean unsetArtifact(int slot) {
        // マテリアルを削除
        for (int i = FIRST_MATERIAL_SLOT_INDEX; i < LAST_MATERIAL_SLOT_INDEX; i++) {
            inv.setItem(i, null);
        }
        changeTitle(inactiveTitle);
        return true;
    }

    /**
     * @param materialItem マテリアルアイテム
     * @param slot         スロット番号 (12~15)
     */
    private boolean setMaterial(ItemStack materialItem, int slot) {
        // マテリアルを取ったとき
        // 魔法武器が置いてなかったらキャンセル
        ItemStack magicStuff = getArtifactItem();
        if (magicStuff == null) return false;

        // 魔法武器のNBT入手
        ArtifactNBT artifactNBT = ArtifactNBT.getMincraNBT(magicStuff);
        if (artifactNBT == null) return false;

        if (materialItem != null) {
            String oraxenMaterialItemId = OraxenItems.getIdByItem(materialItem);
            if (!OraxenItems.exists(materialItem)) return false;
            if (!materialManager.isRegistered(oraxenMaterialItemId)) return false;
            if (!artifactNBT.isAvailableMaterial(oraxenMaterialItemId)) return false;

            artifactNBT.setMaterial(MaterialSlot.fromIndexAsString(slot - FIRST_MATERIAL_SLOT_INDEX), oraxenMaterialItemId);
        } else {
            artifactNBT.removeMaterial(MaterialSlot.fromIndexAsString(slot - FIRST_MATERIAL_SLOT_INDEX));
        }

        ItemStack newItem = artifactNBT.setNBTTag(new ItemBuilder(magicStuff)).build();
        setArtifact(newItem);

        return true;
    }

    private boolean unsetMaterial(int slot) {
        // マテリアルを削除
        setMaterial(null, slot);

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
