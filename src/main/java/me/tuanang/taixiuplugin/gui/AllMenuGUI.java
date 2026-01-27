package me.tuanang.taixiuplugin.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class AllMenuGUI {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, "§6CƯỢC TẤT TAY");

        inv.setItem(3, item(Material.LIME_WOOL, "§aALL TÀI"));
        inv.setItem(5, item(Material.RED_WOOL, "§cALL XỈU"));

        p.openInventory(inv);
    }

    private static ItemStack item(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        i.setItemMeta(meta);
        return i;
    }
}
