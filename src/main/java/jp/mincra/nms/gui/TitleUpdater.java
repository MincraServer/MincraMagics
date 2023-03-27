package jp.mincra.nms.gui;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleUpdater {
    public static void update(Player p, String title, ContainerType containerType){
//        EntityPlayer ep = ((CraftPlayer)p).getHandle();
//        IChatBaseComponent invTitle = IChatBaseComponent.ChatSerializer.a(title);
//        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(
//                // ep.activeContainer.windowId
//                ep.bU.j,
//                containerType.getContainers(), invTitle);
//        // ep.playerConnection
//        ep.b.a(packet);
//        // ep.updateInventory
//        ep.a(ep.bU);
    }
}
