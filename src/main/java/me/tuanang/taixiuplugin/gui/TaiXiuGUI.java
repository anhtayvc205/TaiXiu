package me.tuanang.taixiuplugin.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class TaiXiuGUI {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6TÀI XỈU");

        inv.setItem(11, item(Material.LIME_WOOL, "§aCƯỢC TÀI"));
        inv.setItem(15, item(Material.RED_WOOL, "§cCƯỢC XỈU"));

        p.openInventory(inv);
    }

    private static ItemStack item(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        i.setItemMeta(meta);
        return i;
    }
}l
