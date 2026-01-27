package me.tuanang.taixiuplugin.commands;

import me.tuanang.taixiuplugin.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AllCommand implements CommandExecutor {

    private final TaiXiuPlugin plugin;

    public AllCommand(TaiXiuPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;
        if (a.length == 0) return false;

        double money = plugin.currency().has(p, 0) ? plugin.currency().has(p, 0) : 0;
        double max = plugin.getConfig().getDouble("bet.max");
        if (money > max) money = max;

        if (!plugin.game().check(p, money)) return true;

        plugin.currency().withdraw(p, money);
        plugin.game().bet(p, a[0].equalsIgnoreCase("tai") ? "TAI" : "XIU", money);

        return true;
    }
}
