package me.tuanang.tuanangplugin.anvilgui;

import me.tuanang.tuanangplugin.anvilgui.version.VersionMatcher;
import me.tuanang.tuanangplugin.anvilgui.version.VersionWrapper;
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

    private static final ItemStack AIR;
    private static final VersionWrapper WRAPPER;

    static {
        WRAPPER = new VersionMatcher().match();
        AIR = new ItemStack(Material.AIR);
    }

    private final Plugin plugin;
    private final Player player;
    private final Executor mainThreadExecutor;
    private final Object titleComponent;
    private final ItemStack[] initialContents;
    private final boolean preventClose;
    private final boolean geyserCompatibility;
    private final Set<Integer> interactableSlots;
    private final Consumer<StateSnapshot> closeListener;
    private final boolean concurrentClickHandlerExecution;
    private final ClickHandler clickHandler;

    private VersionWrapper.AnvilContainerWrapper container;
    private int containerId;
    private Inventory inventory;
    private boolean open;

    private final ListenUp listener = new ListenUp(this);

    private AnvilGUI(
            Plugin plugin,
            Player player,
            Executor executor,
            Object titleComponent,
            ItemStack[] initialContents,
            boolean preventClose,
            boolean geyserCompatibility,
            Set<Integer> interactableSlots,
            Consumer<StateSnapshot> closeListener,
            boolean concurrentClickHandlerExecution,
            ClickHandler clickHandler
    ) {
        this.plugin = plugin;
        this.player = player;
        this.mainThreadExecutor = executor;
        this.titleComponent = titleComponent;
        this.initialContents = initialContents;
        this.preventClose = preventClose;
        this.geyserCompatibility = geyserCompatibility;
        this.interactableSlots = Collections.unmodifiableSet(interactableSlots);
        this.closeListener = closeListener;
        this.concurrentClickHandlerExecution = concurrentClickHandlerExecution;
        this.clickHandler = clickHandler;
    }

    private static ItemStack itemNotNull(ItemStack item) {
        return item == null ? AIR : item;
    }

    private void openInventory() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        this.container = WRAPPER.newContainerAnvil(player, titleComponent);
        this.inventory = container.getBukkitInventory();

        for (int i = 0; i < initialContents.length; i++) {
            inventory.setItem(i, itemNotNull(initialContents[i]));
        }

        this.containerId = WRAPPER.getNextContainerId(player, container);
        WRAPPER.handleInventoryCloseEvent(player);
        WRAPPER.sendPacketOpenWindow(player, containerId, titleComponent);
        WRAPPER.setActiveContainer(player, container);
        WRAPPER.setActiveContainerId(container, containerId);

        this.open = true;
    }

    public void closeInventory() {
        if (!open) return;
        open = false;

        HandlerList.unregisterAll(listener);
        WRAPPER.sendPacketCloseWindow(player, containerId);
        WRAPPER.setActiveContainerDefault(player);

        if (closeListener != null) {
            closeListener.accept(snapshot());
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setTitle(String title) {
        setJsonTitle("{\"text\":\"" + title + "\"}");
    }

    public void setJsonTitle(String json) {
        Object component = WRAPPER.jsonChatComponent(json);
        WRAPPER.sendPacketOpenWindow(player, containerId, component);
    }

    private StateSnapshot snapshot() {
        return new StateSnapshot(
                inventory.getItem(0),
                inventory.getItem(1),
                inventory.getItem(2),
                player
        );
    }

    /* =========================
       BUILDER
       ========================= */

    public static class Builder {

        private Plugin plugin;
        private Executor mainThreadExecutor;
        private Object titleComponent = WRAPPER.literalChatComponent("Repair & Name");

        private ItemStack itemLeft;
        private ItemStack itemRight;
        private ItemStack itemOutput;
        private String itemText;

        private boolean concurrentClickHandlerExecution;
        private boolean preventClose;
        private boolean geyserCompatibility = true;

        private Set<Integer> interactableSlots = Collections.emptySet();
        private Consumer<StateSnapshot> closeListener;
        private ClickHandler clickHandler;

        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder text(String text) {
            this.itemText = text;
            return this;
        }

        public Builder itemLeft(ItemStack item) {
            this.itemLeft = item;
            return this;
        }

        public Builder itemRight(ItemStack item) {
            this.itemRight = item;
            return this;
        }

        public Builder itemOutput(ItemStack item) {
            this.itemOutput = item;
            return this;
        }

        public Builder onClick(BiFunction<Integer, StateSnapshot, List<ResponseAction>> fn) {
            this.clickHandler = (slot, state) ->
                    CompletableFuture.completedFuture(fn.apply(slot, state));
            return this;
        }

        public Builder onClickAsync(ClickHandler handler) {
            this.clickHandler = handler;
            return this;
        }

        public Builder onClose(Consumer<StateSnapshot> listener) {
            this.closeListener = listener;
            return this;
        }

        public Builder preventClose() {
            this.preventClose = true;
            return this;
        }

        public Builder disableGeyserCompat() {
            this.geyserCompatibility = false;
            return this;
        }

        public Builder allowConcurrentClickHandlerExecution() {
            this.concurrentClickHandlerExecution = true;
            return this;
        }

        public AnvilGUI open(Player player) {
            if (mainThreadExecutor == null) {
                mainThreadExecutor = r -> Bukkit.getScheduler().runTask(plugin, r);
            }

            ItemStack[] contents = new ItemStack[]{itemLeft, itemRight, itemOutput};

            AnvilGUI gui = new AnvilGUI(
                    plugin,
                    player,
                    mainThreadExecutor,
                    titleComponent,
                    contents,
                    preventClose,
                    geyserCompatibility,
                    interactableSlots,
                    closeListener,
                    concurrentClickHandlerExecution,
                    clickHandler
            );

            gui.openInventory();
            return gui;
        }
    }

    /* =========================
       SUPPORT
       ========================= */

    @FunctionalInterface
    public interface ClickHandler
            extends BiFunction<Integer, StateSnapshot, CompletableFuture<List<ResponseAction>>> {
    }

    private static class ListenUp implements Listener {
        private final AnvilGUI gui;

        private ListenUp(AnvilGUI gui) {
            this.gui = gui;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!e.getWhoClicked().equals(gui.player)) return;
            if (!e.getInventory().equals(gui.inventory)) return;
            e.setCancelled(true);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (e.getPlayer().equals(gui.player)) {
                gui.closeInventory();
            }
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getWhoClicked().equals(gui.player)) {
                e.setCancelled(true);
            }
        }
    }

    public static class Slot {
        public static final int INPUT_LEFT = 0;
        public static final int INPUT_RIGHT = 1;
        public static final int OUTPUT = 2;
    }

    public static final class StateSnapshot {
        private final ItemStack leftItem;
        private final ItemStack rightItem;
        private final ItemStack outputItem;
        private final Player player;

        public StateSnapshot(ItemStack left, ItemStack right, ItemStack output, Player player) {
            this.leftItem = left;
            this.rightItem = right;
            this.outputItem = output;
            this.player = player;
        }

        public ItemStack getLeftItem() { return leftItem; }
        public ItemStack getRightItem() { return rightItem; }
        public ItemStack getOutputItem() { return outputItem; }
        public Player getPlayer() { return player; }

        public String getText() {
            if (outputItem != null && outputItem.hasItemMeta()) {
                return outputItem.getItemMeta().getDisplayName();
            }
            return "";
        }
    }

    public interface ResponseAction extends BiConsumer<AnvilGUI, Player> {
        static ResponseAction close() {
            return (gui, p) -> gui.closeInventory();
        }

        static ResponseAction openInventory(Inventory inv) {
            return (gui, p) -> p.openInventory(inv);
        }

        static ResponseAction replaceInputText(String text) {
            return (gui, p) -> {
                ItemStack item = gui.inventory.getItem(0);
                if (item == null) return;
                ItemStack clone = item.clone();
                ItemMeta meta = clone.getItemMeta();
                meta.setDisplayName(text);
                clone.setItemMeta(meta);
                gui.inventory.setItem(0, clone);
            };
        }
    }
}
