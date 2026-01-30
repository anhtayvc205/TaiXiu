package me.taixiu.command;

import me.taixiu.TaiXiuPlugin;
import me.taixiu.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;

        PlayerData d = TaiXiuPlugin.instance.data.get(p);

        p.sendMessage("§6§lTÀI XỈU STATS");
        p.sendMessage("§7Win: §a" + d.getWins());
        p.sendMessage("§7Lose: §c" + d.getLoses());
        p.sendMessage("§7Tổng cược: §e" + d.getTotalBet());
        p.sendMessage("§7Lời/Lỗ: §b" + d.getProfit());

        return true;
    }
}
