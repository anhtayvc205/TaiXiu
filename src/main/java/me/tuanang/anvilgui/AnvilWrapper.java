package me.tuanang.tuanangplugin.anvilgui;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public final class AnvilWrapper {

    private static EntityPlayer nms(Player p) {
        return ((CraftPlayer) p).getHandle();
    }

    public static ContainerAnvil newContainer(Player p, Object title) {
        return new ContainerAnvil(
                nms(p).nextContainerCounter(),
                nms(p).gs(),
                ContainerAccess.a(
                        ((CraftWorld) p.getWorld()).getHandle(),
                        new BlockPosition(0, 0, 0)
                )
        ) {{
            checkReachable = false;
            setTitle((IChatBaseComponent) title);
        }};
    }

    public static void open(Player p, ContainerAnvil container, Object title) {
        EntityPlayer ep = nms(p);
        PlayerConnection conn = ep.g;
        int id = container.l;

        conn.b(new PacketPlayOutOpenWindow(id, Containers.i, (IChatBaseComponent) title));
        ep.cn = container;
        ep.a(container);
    }

    public static void close(Player p, int id) {
        nms(p).g.b(new PacketPlayOutCloseWindow(id));
    }

    public static void reset(Player p) {
        EntityPlayer ep = nms(p);
        ep.cn = ep.cm;
    }

    public static Object text(String text) {
        return IChatBaseComponent.b(text);
    }

    public static Object json(String json) {
        return CraftChatMessage.fromJSON(json);
    }

    public static Inventory top(Container container) {
        InventoryView view = container.getBukkitView();
        return view.getTopInventory();
    }

    public static void setRename(ContainerAnvil c, String text) {
        Slot left = c.b(0);
        if (left.h()) {
            ItemStack item = left.g();
            item.b(net.minecraft.core.component.DataComponents.g,
                    IChatBaseComponent.b(text));
        }
    }
}
