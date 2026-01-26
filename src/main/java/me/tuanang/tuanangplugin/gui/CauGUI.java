package me.tuanang.tuanangplugin.gui;

import me.tuanang.tuanangplugin.TaiXiuPlugin;
import me.tuanang.tuanangplugin.managers.RoundManager;
import me.tuanang.tuanangplugin.managers.RoundManager.RoundHistory;
import me.tuanang.tuanangplugin.managers.RoundManager.Result;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CauGUI {

    public CauGUI() {
        super();
    }

    public static String getTitle() {
        return "Lịch sử Tài Xỉu";
    }

    public static void open(Player player) {

        Inventory inv = Bukkit.createInventory(
                (InventoryHolder) null,
                27,
                "§e" + getTitle()
        );

        RoundManager rm = TaiXiuPlugin.getInstance().getRoundManager();
        List<RoundHistory> history = rm.getDetailedHistory();
        int roundNumber = rm.getRoundNumber();

        for (int i = 0; i < Math.min(history.size(), 27); i++) {

            RoundHistory h = history.get(i);
            Result result = h.result;

            Material mat = (result == Result.TAI)
                    ? Material.GRAY_STAINED_GLASS_PANE
                    : Material.WHITE_STAINED_GLASS_PANE;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();

            String side = (result == Result.TAI) ? "§7Tài" : "§fXỉu";
            int round = roundNumber - 1 - i;

            meta.setDisplayName(side + " §7(Phiên #" + round + ")");

            int d1 = h.dice[0];
            int d2 = h.dice[1];
            int d3 = h.dice[2];
            int total = d1 + d2 + d3;

            String jackpotText = h.jackpot ? "§d(💥 Nổ hũ!)" : "";

            String lore = String.format(
                    "§7🎲 %d + %d + %d = %d %s",
                    d1, d2, d3, total, jackpotText
            );

            meta.setLore(List.of(lore));
            item.setItemMeta(meta);

            inv.setItem(i, item);
        }

        player.openInventory(inv);
    }
}
