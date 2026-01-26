package me.tuanang.tuanangplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AnvilGUI implements Listener {

    private final Plugin plugin;
    private final Player player;
    private final Inventory inv;

    private Consumer<String> completeHandler;
    private Runnable closeHandler;

    public AnvilGUI(Plugin plugin, Player player, String title) {
        this.plugin = plugin;
        this.player = player;
        this.inv = Bukkit.createInventory(null, 3, title);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /* ================== SETUP ================== */

    public AnvilGUI setLeft(ItemStack item) {
        inv.setItem(0, item);
        return this;
    }

    public AnvilGUI setRight(ItemStack item) {
        inv.setItem(1, item);
        return this;
    }

    public AnvilGUI setOutput(ItemStack item) {
        inv.setItem(2, item);
        return this;
    }

    public AnvilGUI onComplete(Consumer<String> handler) {
        this.completeHandler = handler;
        return this;
    }

    public AnvilGUI onClose(Runnable handler) {
        this.closeHandler = handler;
        return this;
    }

    /* ================== OPEN ================== */

    public void open() {
        player.openInventory(inv);
    }

    /* ================== EVENTS ================== */

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        if (!e.getWhoClicked().equals(player)) return;

        e.setCancelled(true);

        if (e.getSlot() != 2) return;

        ItemStack item = inv.getItem(2);
        if (item == null || !item.hasItemMeta()) return;

        String text = item.getItemMeta().getDisplayName();
        if (completeHandler != null) {
            completeHandler.accept(text);
        }

        player.closeInventory();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv)) return;
        if (!e.getPlayer().equals(player)) return;

        if (closeHandler != null) {
            Bukkit.getScheduler().runTask(plugin, closeHandler);
        }

        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    /* ================== UTIL ================== */

    public static ItemStack renameItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
