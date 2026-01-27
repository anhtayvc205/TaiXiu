package me.tuanang.taixiuplugin.listeners;

import me.tuanang.taixiuplugin.TaiXiuPlugin;
import me.tuanang.taixiuplugin.gui.AllMenuGUI;
import me.tuanang.taixiuplugin.gui.TaiXiuGUI;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final TaiXiuPlugin plugin;

    public InventoryListener(TaiXiuPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getCurrentItem() == null) return;

        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();

        // GUI chính
        if (title.equals("§6TÀI XỈU")) {
            e.setCancelled(true);

            if (item.getType().name().contains("LIME")) {
                p.performCommand("tai 1000");
            } else if (item.getType().name().contains("RED")) {
                p.performCommand("xiu 1000");
            }
        }

        // GUI ALL
        if (title.equals("§6CƯỢC TẤT TAY")) {
            e.setCancelled(true);

            if (item.getType().name().contains("LIME")) {
                p.performCommand("all tai");
            } else if (item.getType().name().contains("RED")) {
                p.performCommand("all xiu");
            }
        }
    }
}
