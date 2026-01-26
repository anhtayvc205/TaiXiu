package me.tuanang.tuanangplugin.listener;

import me.tuanang.tuanangplugin.gui.CauGUI;
import me.tuanang.tuanangplugin.gui.TaixiuGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    public InventoryListener() {
        super();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String title = ChatColor.stripColor(event.getView().getTitle());

        String guiTitle = ChatColor.stripColor(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        TaixiuGUI.guiConfig.getString("title")
                )
        );

        // Main Taixiu GUI
        if (title.equals(guiTitle)) {
            event.setCancelled(true);
            TaixiuGUI.handleInventoryClick(event);
            return;
        }

        // Cau history GUI
        if (title.equals(CauGUI.getTitle())) {
            event.setCancelled(true);
        }
    }
}
