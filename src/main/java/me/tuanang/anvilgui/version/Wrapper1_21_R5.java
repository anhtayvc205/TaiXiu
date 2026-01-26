package me.tuanang.tuanangplugin.anvilgui.version;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutExperience;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
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

public final class Wrapper1_21_R5 implements VersionWrapper {

    public Wrapper1_21_R5() {}

    /* ================= UTIL ================= */

    private EntityPlayer toNMS(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    private int getRealNextContainerId(Player player) {
        return toNMS(player).nextContainerCounter();
    }

    /* ================= VersionWrapper ================= */

    @Override
    public VersionWrapper.AnvilContainerWrapper newContainerAnvil(Player player, Object title) {
        return new AnvilContainer(
                player,
                getRealNextContainerId(player),
                (IChatBaseComponent) title
        );
    }

    @Override
    public int getNextContainerId(Player player, VersionWrapper.AnvilContainerWrapper container) {
        return ((AnvilContainer) container).getContainerId();
    }

    @Override
    public void sendPacketOpenWindow(Player player, int id, Object title) {
        EntityPlayer nms = toNMS(player);
        PlayerConnection conn = nms.g;

        conn.b(new PacketPlayOutOpenWindow(
                id,
                Containers.i,
                (IChatBaseComponent) title
        ));
    }

    @Override
    public void sendPacketCloseWindow(Player player, int id) {
        EntityPlayer nms = toNMS(player);
        nms.g.b(new PacketPlayOutCloseWindow(id));
    }

    @Override
    public void sendPacketExperienceChange(Player player, int level) {
        EntityPlayer nms = toNMS(player);
        nms.g.b(new PacketPlayOutExperience(0f, 0, level));
    }

    @Override
    public void setActiveContainer(Player player, VersionWrapper.AnvilContainerWrapper container) {
        toNMS(player).cn = (Container) container;
    }

    @Override
    public void setActiveContainerDefault(Player player) {
        EntityPlayer nms = toNMS(player);
        nms.cn = nms.cm;
    }

    @Override
    public void setActiveContainerId(VersionWrapper.AnvilContainerWrapper container, int id) {
        // smali: empty
    }

    @Override
    public void addActiveContainerSlotListener(VersionWrapper.AnvilContainerWrapper container, Player player) {
        toNMS(player).a((Container) container);
    }

    @Override
    public void handleInventoryCloseEvent(Player player) {
        // smali không decompile được → để trống cho an toàn
    }

    @Override
    public Object literalChatComponent(String text) {
        return IChatBaseComponent.b(text);
    }

    @Override
    public Object jsonChatComponent(String json) {
        return CraftChatMessage.fromJSON(json);
    }

    /* =========================================================
       INNER CLASS: AnvilContainer
       ========================================================= */

    public static class AnvilContainer extends ContainerAnvil
            implements VersionWrapper.AnvilContainerWrapper {

        public AnvilContainer(Player player, int id, IChatBaseComponent title) {
            super(
                    id,
                    ((CraftPlayer) player).getHandle().gs(),
                    ContainerAccess.a(
                            ((CraftWorld) player.getWorld()).getHandle(),
                            new BlockPosition(0, 0, 0)
                    )
            );
            this.checkReachable = false;
            this.setTitle(title);
        }

        @Override
        public void a(net.minecraft.world.entity.player.EntityHuman human) {}

        @Override
        protected void a(net.minecraft.world.entity.player.EntityHuman human,
                         net.minecraft.world.IInventory inv) {}

        @Override
        public Inventory getBukkitInventory() {
            InventoryView view = getBukkitView();
            return view.getTopInventory();
        }

        @Override
        public int getContainerId() {
            return this.l;
        }

        @Override
        public String getRenameText() {
            return this.x;
        }

        @Override
        public void l() {
            Slot output = this.b(2);
            if (!output.h()) {
                ItemStack clone = this.b(0).g().v();
                output.f(clone);
            }
            this.y.a(0);
            this.b();
            this.d();
        }

        @Override
        public void setRenameText(String text) {
            Slot left = this.b(0);
            if (left.h()) {
                ItemStack item = left.g();
                item.b(
                        net.minecraft.core.component.DataComponents.g,
                        IChatBaseComponent.b(text)
                );
            }
        }
    }
}
