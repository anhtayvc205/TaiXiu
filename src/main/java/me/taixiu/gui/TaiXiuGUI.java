package me.taixiu.gui;

import me.taixiu.game.TaiXiuGame;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class TaiXiuGUI implements Listener {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27,
                ChatColor.GOLD + "TÀI XỈU CASINO");

        inv.setItem(11, item(Material.GREEN_WOOL, "§aTÀI"));
        inv.setItem(15, item(Material.RED_WOOL, "§cXỈU"));
        p.openInventory(inv);
    }

    private static ItemStack item(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        i.setItemMeta(im);
        return i;
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!e.getView().getTitle().contains("TÀI XỈU")) return;
        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        if (e.getCurrentItem().getType() == Material.GREEN_WOOL)
            TaiXiuGame.play(p, "tai");

        if (e.getCurrentItem().getType() == Material.RED_WOOL)
            TaiXiuGame.play(p, "xiu");
    }
}
