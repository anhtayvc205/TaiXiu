package me.tuanang.tuanangplugin.gui;

import me.tuanang.tuanangplugin.TaiXiuPlugin;
import me.tuanang.tuanangplugin.managers.RoundManager;
import me.tuanang.tuanangplugin.utils.EconomyUtil;
import me.tuanang.tuanangplugin.utils.MoneyUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AllBetGUI {

    public static FileConfiguration guiConfig;

    public AllBetGUI() {
        super();
    }

    public static void handleClick(Player player, ItemStack item) {

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();

        boolean isTai;

        if (name.contains("Tài")) {
            isTai = true;
        } else if (name.contains("Xỉu")) {
            isTai = false;
        } else {
            return;
        }

        double balance = EconomyUtil.getBalance(player);
        if (balance <= 0) {
            player.sendMessage("§cBạn không có tiền để cược.");
            return;
        }

        RoundManager rm = TaiXiuPlugin.getInstance().getRoundManager();
        boolean success = rm.placeBet(player, isTai, balance);

        if (!success) return;

        EconomyUtil.withdraw(player, balance);

        String money = MoneyUtils.formatVietMoney(balance);
        String side = isTai ? "§bTài" : "§fXỉu";

        player.sendMessage("§aĐặt cược thành công toàn bộ " + money + "đ vào " + side + ".");
    }

    public static void openAllGuiMenu(Player player) {

        Inventory inv = Bukkit.createInventory(
                (InventoryHolder) null,
                27,
                "§6Cược tất cả tiền"
        );

        ItemStack tai = new ItemStack(Material.BLUE_WOOL);
        ItemMeta taiMeta = tai.getItemMeta();
        taiMeta.setDisplayName("§bCược tất cả vào Tài");
        taiMeta.setLore(Arrays.asList("§7Click để đặt toàn bộ tiền vào §bTài"));
        tai.setItemMeta(taiMeta);
        inv.setItem(11, tai);

        ItemStack xiu = new ItemStack(Material.WHITE_WOOL);
        ItemMeta xiuMeta = xiu.getItemMeta();
        xiuMeta.setDisplayName("§fCược tất cả vào Xỉu");
        xiuMeta.setLore(Arrays.asList("§7Click để đặt toàn bộ tiền vào §fXỉu"));
        xiu.setItemMeta(xiuMeta);
        inv.setItem(15, xiu);

        player.openInventory(inv);
    }

    public static void reload() {

        File file = new File(
                TaiXiuPlugin.getInstance().getDataFolder(),
                "guid/taixiu_all.yml"
        );

        guiConfig = YamlConfiguration.loadConfiguration(file);
    }
}
