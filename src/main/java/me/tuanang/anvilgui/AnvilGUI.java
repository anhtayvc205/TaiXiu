package me.tuanang.tuanangplugin.anvilgui;

import net.minecraft.world.inventory.ContainerAnvil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class AnvilGUI {

    private static final ItemStack AIR = new ItemStack(Material.AIR);

    private final Plugin plugin;
    private final Player player;
    private final Executor executor;
    private final Object titleComponent;
    private final ItemStack[] initialContents;
    private final boolean preventClose;
    private final Consumer<StateSnapshot> closeListener;
    private final ClickHandler clickHandler;

    private ContainerAnvil container;
    private Inventory inventory;
    private boolean open;

    private final Listener listener = new Listener() {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (!open) return;
            if (!e.getWhoClicked().equals(player)) return;
            if (!e.getInventory().equals(inventory)) return;

            e.setCancelled(true);

            int slot = e.getRawSlot();
            if (slot < 0 || slot > 2) return;
            if (clickHandler == null) return;

            StateSnapshot snapshot = snapshot();

            clickHandler.apply(slot, snapshot).thenAccept(list -> {
                for (ResponseAction a : list) {
                    executor.execute(() -> a.accept(AnvilGUI.this, player));
                }
            });
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            if (!open) return;
            if (!e.getPlayer().equals(player)) return;

            open = false;
            HandlerList.unregisterAll(this);

            if (preventClose) {
                Bukkit.getScheduler().runTask(plugin, () -> open(player));
                return;
            }

            if (closeListener != null)
                closeListener.accept(snapshot());

            AnvilWrapper.reset(player);
        }
    };

    private AnvilGUI(
            Plugin plugin,
            Player player,
            Executor executor,
            Object titleComponent,
            ItemStack[] initialContents,
            boolean preventClose,
            Consumer<StateSnapshot> closeListener,
            ClickHandler clickHandler
    ) {
        this.plugin = plugin;
        this.player = player;
        this.executor = executor;
        this.titleComponent = titleComponent;
        this.initialContents = initialContents;
        this.preventClose = preventClose;
        this.closeListener = closeListener;
        this.clickHandler = clickHandler;
    }

    private StateSnapshot snapshot() {
        return new StateSnapshot(
                inventory.getItem(0),
                inventory.getItem(1),
                inventory.getItem(2),
                player
        );
    }

    private void open(Player p) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        container = AnvilWrapper.newContainer(p, titleComponent);
        inventory = AnvilWrapper.top(container);

        for (int i = 0; i < initialContents.length; i++)
            inventory.setItem(i, initialContents[i]);

        AnvilWrapper.open(p, container, titleComponent);
        open = true;
    }

    /* ================= BUILDER ================= */

    public static class Builder {

        private Plugin plugin;
        private Object title = AnvilWrapper.text("Repair & Name");
        private ItemStack left, right, out;
        private boolean preventClose;
        private Consumer<StateSnapshot> closeListener;
        private ClickHandler clickHandler;

        public Builder plugin(Plugin p) {
            this.plugin = p;
            return this;
        }

        public Builder title(String text) {
            this.title = AnvilWrapper.text(text);
            return this;
        }

        public Builder itemLeft(ItemStack i) {
            this.left = i;
            return this;
        }

        public Builder itemRight(ItemStack i) {
            this.right = i;
            return this;
        }

        public Builder itemOutput(ItemStack i) {
            this.out = i;
            return this;
        }

        public Builder preventClose() {
            this.preventClose = true;
            return this;
        }

        public Builder onClose(Consumer<StateSnapshot> c) {
            this.closeListener = c;
            return this;
        }

        public Builder onClick(BiFunction<Integer, StateSnapshot, List<ResponseAction>> fn) {
            this.clickHandler = (slot, state) ->
                    CompletableFuture.completedFuture(fn.apply(slot, state));
            return this;
        }

        public AnvilGUI open(Player p) {
            Executor exec = r -> Bukkit.getScheduler().runTask(plugin, r);

            AnvilGUI gui = new AnvilGUI(
                    plugin,
                    p,
                    exec,
                    title,
                    new ItemStack[]{left, right, out},
                    preventClose,
                    closeListener,
                    clickHandler
            );

            gui.open(p);
            return gui;
        }
    }

    /* ================= MODELS ================= */

    public record StateSnapshot(ItemStack left, ItemStack right, ItemStack output, Player player) {
        public String text() {
            if (output != null && output.hasItemMeta())
                return output.getItemMeta().getDisplayName();
            return "";
        }
    }

    @FunctionalInterface
    public interface ClickHandler
            extends BiFunction<Integer, StateSnapshot, CompletableFuture<List<ResponseAction>>> {
    }

    public interface ResponseAction extends BiConsumer<AnvilGUI, Player> {
        static ResponseAction close() {
            return (g, p) -> p.closeInventory();
        }

        static ResponseAction text(String text) {
            return (g, p) -> AnvilWrapper.setRename(g.container, text);
        }

        static ResponseAction open(Inventory inv) {
            return (g, p) -> p.openInventory(inv);
        }
    }
}
