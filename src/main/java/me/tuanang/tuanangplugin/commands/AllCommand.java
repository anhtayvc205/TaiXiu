package me.tuanang.tuanangplugin.commands;

import me.tuanang.tuanangplugin.TaiXiuPlugin;
import me.tuanang.tuanangplugin.managers.RoundManager;
import me.tuanang.tuanangplugin.utils.EconomyUtil;
import me.tuanang.tuanangplugin.utils.MoneyUtils;

import org.bukkit.Sound;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

public class AllCommand implements CommandExecutor {

    public AllCommand() {
        super();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean tai = false;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage("§cDùng: /all <tai|xiu>");
                return true;
            }

            String choice = args[0].toLowerCase();

            if (choice.equals("tai")) {
                tai = true;
            } else if (!choice.equals("xiu")) {
                player.sendMessage("§cLựa chọn không hợp lệ. Hãy dùng: /all <tai|xiu>");
                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            double balance = EconomyUtil.getBalance((OfflinePlayer) player);
            if (balance <= 0) {
                player.sendMessage("§cBạn không có tiền để cược.");
                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            RoundManager rm = TaiXiuPlugin.getInstance().getRoundManager();
            boolean success = rm.placeBet(player, tai, balance);

            if (success) {
                EconomyUtil.withdraw((OfflinePlayer) player, balance);

                String money = MoneyUtils.formatVietMoney(balance);
                String side = tai ? "§bTài" : "§fXỉu";
                player.sendMessage("§aĐặt cược toàn bộ " + money + " đ vào " + side + ".");

                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }

            return true;
        }

        sender.sendMessage("Chỉ người chơi mới dùng lệnh này.");
        return true;
    }
}
