package me.tuanang.taixiuplugin.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CauGUI {

    public static void open(Player p, List<String> history) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6CẦU TÀI XỈU");

        int slot = 0;
        for (String s : history) {
            Material m = s.equals("TAI") ? Material.LIME_WOOL : Material.RED_WOOL;
            ItemStack i = new ItemStack(m);
            ItemMeta meta = i.getItemMeta();
            meta.setDisplayName(s.equals("TAI") ? "§aTÀI" : "§cXỈU");
            i.setItemMeta(meta);
            inv.setItem(slot++, i);
        }

        p.openInventory(inv);
    }
}
