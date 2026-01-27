package me.tuanang.taixiuplugin.commands;

import me.tuanang.taixiuplugin.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TaiCommand implements CommandExecutor {

    private final TaiXiuPlugin plugin;

    public TaiCommand(TaiXiuPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;
        if (a.length == 0) return false;

        double money = Double.parseDouble(a[0]);

        if (!plugin.game().check(p, money)) return true;
        if (!plugin.currency().has(p, money)) {
            p.sendMessage("§cBạn không đủ tiền!");
            return true;
        }

        plugin.currency().withdraw(p, money);
        plugin.game().bet(p, "TAI", money);

        p.sendMessage("§aĐã cược TÀI: §e" + money);
        return true;
    }
}
