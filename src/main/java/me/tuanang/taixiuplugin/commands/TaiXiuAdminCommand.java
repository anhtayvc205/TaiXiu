package me.tuanang.taixiuplugin.commands;

import me.tuanang.taixiuplugin.TaiXiuPlugin;
import org.bukkit.command.*;

public class TaiXiuAdminCommand implements CommandExecutor {

    private final TaiXiuPlugin plugin;

    public TaiXiuAdminCommand(TaiXiuPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!s.hasPermission("taixiu.admin")) return true;

        if (a.length == 0) {
            s.sendMessage("/taixiuadmin reload");
            return true;
        }

        if (a[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            s.sendMessage("§aĐã reload config Tài Xỉu");
        }
        return true;
    }
}
