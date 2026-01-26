package me.tuanang.tuanangplugin.listener;

import me.tuanang.tuanangplugin.gui.AllBetGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    public GUIListener() {
        super();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        InventoryView view = event.getView();
        String title = view.getTitle();

        if (!title.equals("§6Cược tất cả tiền")) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        Inventory clicked = event.getClickedInventory();
        Inventory top = view.getTopInventory();

        if (clicked != top) return;

        AllBetGUI.handleClick(player, item);
    }
}
