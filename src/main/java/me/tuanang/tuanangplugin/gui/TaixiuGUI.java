package me.tuanang.tuanangplugin.gui;

import me.tuanang.tuanangplugin.TuanAngPlugin;
import me.tuanang.tuanangplugin.managers.RoundManager;
import me.tuanang.tuanangplugin.utils.MoneyUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import org.kazamistudio.anvilgui.AnvilGUI;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class TaixiuGUI {

    public static FileConfiguration guiConfig;

    static {
        File dir = new File(
                TaiXiuPlugin.getInstance().getDataFolder(),
                "guid"
        );

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "taixiugui.yml");
        if (!file.exists()) {
            TuanAngPlugin.getInstance().saveResource("guid/taixiugui.yml", false);
        }

        guiConfig = YamlConfiguration.loadConfiguration(file);
    }

    public TaixiuGUI() {
        super();
    }

    public static String getTitle() {
        return ChatColor.translateAlternateColorCodes(
                '&',
                guiConfig.getString("title", "Chọn Tài/Xỉu")
        );
    }

    public static void handleInventoryClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();

        String guiTitle = ChatColor.translateAlternateColorCodes(
                '&',
                guiConfig.getString("title", "Chọn Tài/Xỉu")
        );

        if (!title.equals(guiTitle)) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (name.contains("Cược Tài")) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(
                    TuanAngPlugin.getInstance(),
                    () -> openAnvil(player, true),
                    1L
            );
            return;
        }

        if (name.contains("Cược Xỉu")) {
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(
                    TuanAngPlugin.getInstance(),
                    () -> openAnvil(player, false),
                    1L
            );
        }
    }

    public static void openAnvil(Player player, boolean tai) {

        new AnvilGUI.Builder()
                .plugin(TuanAngPlugin.getInstance())
                .title("Nhập số tiền cược")
                .text("1000")
                .onClick((slot, state) -> {
                    if (slot != 2) {
                        return AnvilGUI.Response.close();
                    }

                    try {
                        double money = Double.parseDouble(state.getText());
                        RoundManager rm = TuanAngPlugin.getInstance().getRoundManager();

                        boolean success = rm.placeBet(player, tai, money);
                        if (success) {
                            String side = tai ? "Tài" : "Xỉu";
                            String msg = ChatColor.GREEN + "✅ Bạn đã cược "
                                    + MoneyUtils.formatVietMoney(money)
                                    + "đ vào " + side + ".";

                            player.sendMessage(msg);
                            player.playSound(
                                    player.getLocation(),
                                    Sound.ENTITY_PLAYER_LEVELUP,
                                    1.0f,
                                    1.0f
                            );
                        }
                        return AnvilGUI.Response.close();
                    } catch (NumberFormatException ex) {
                        return AnvilGUI.Response.text("Chỉ nhập số!");
                    }
                })
                .open(player);
    }

    private static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void openMainMenu(Player player) {

        String title = translate(guiConfig.getString("title", "Chọn Tài/Xỉu"));
        int rows = guiConfig.getInt("rows", 3);

        Inventory inv = Bukkit.createInventory(
                (InventoryHolder) null,
                rows * 9,
                title
        );

        ConfigurationSection items = guiConfig.getConfigurationSection("items");
        if (items == null) {
            player.openInventory(inv);
            return;
        }

        for (String key : items.getKeys(false)) {
            ConfigurationSection sec = items.getConfigurationSection(key);
            if (sec == null) continue;

            Material mat = Material.getMaterial(
                    sec.getString("material", "STONE")
            );
            if (mat == null) continue;

            int slot = sec.getInt("slot", 0);
            String name = translate(sec.getString("name", key));

            List<String> lore = new ArrayList<>(
                    sec.getStringList("lore")
                            .stream()
                            .map(TaixiuGUI::translate)
                            .toList()
            );

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
        }

        player.openInventory(inv);
    }

    public static void reload() {
        File file = new File(
                TaiXiuPlugin.getInstance().getDataFolder(),
                "guid/taixiugui.yml"
        );
        guiConfig = YamlConfiguration.loadConfiguration(file);
    }
}
