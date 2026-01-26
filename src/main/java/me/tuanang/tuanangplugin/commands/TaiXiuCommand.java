package me.tuanang.tuanangplugin.commands;

import me.tuanang.tuanangplugin.TaiXiuPlugin;
import me.tuanang.tuanangplugin.gui.AllBetGUI;
import me.tuanang.tuanangplugin.gui.TaixiuGUI;
import me.tuanang.tuanangplugin.managers.RoundManager;
import me.tuanang.tuanangplugin.utils.ColorUtils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TaiXiuCommand implements CommandExecutor, TabCompleter {

    public TaiXiuCommand() {
        super();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("taixiu.admin")) {
            sender.sendMessage("§cBạn không có quyền sử dụng lệnh này!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§eSử dụng: /" + label + " reload | clear");
            return true;
        }

        String sub = args[0];

        if (sub.equalsIgnoreCase("reload")) {

            TaiXiuPlugin.getInstance().reloadConfig();
            TaixiuGUI.reload();
            AllBetGUI.reload();

            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage(ColorUtils.translate("&a[✓] Đã reload thành công."));
                Location loc = p.getLocation();
                p.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            } else {
                sender.sendMessage(ColorUtils.translate("&a[✓] Đã reload thành công."));
            }

            return true;
        }

        if (sub.equalsIgnoreCase("clear")) {

            RoundManager rm = TaiXiuPlugin.getInstance().getRoundManager();
            rm.clearHistory();
            sender.sendMessage("§c[!] Dữ liệu đã được xoá sạch.");
            return true;
        }

        if (sub.equalsIgnoreCase("payout")) {

            RoundManager rm = TaiXiuPlugin.getInstance().getRoundManager();
            double jackpot = rm.getJackpot();

            if (jackpot <= 0) {
                sender.sendMessage("§c[!] Hũ hiện tại đang trống.");
                return true;
            }

            rm.clearJackpot();
            sender.sendMessage("§a[✓] Đã xả toàn bộ hũ: " + jackpot + "đ.");
            return true;
        }

        sender.sendMessage("§eSử dụng: /" + label + " reload | clear");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("taixiu.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            List<String> subs = Arrays.asList("reload", "clear", "payout");

            for (String s : subs) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(s);
                }
            }
            return list;
        }

        return Collections.emptyList();
    }
}
