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

public abstract class BetCommand implements CommandExecutor {

    public BetCommand() {
        super();
    }

    protected abstract boolean isTai();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        final double MIN_BET = 1000.0;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage("§cDùng: /" + label + " <số tiền>");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cSố tiền không hợp lệ.");
                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            if (amount <= 0) {
                player.sendMessage("§cSố tiền phải lớn hơn 0.");
                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            if (amount < MIN_BET) {
                String min = MoneyUtils.formatVietMoney(MIN_BET);
                player.sendMessage("§cMức cược tối thiểu là " + min + "đ.");
                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            double balance = EconomyUtil.getBalance((OfflinePlayer) player);
            if (balance < amount) {
                player.sendMessage("§cBạn không đủ tiền.");
                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return true;
            }

            RoundManager rm = TaiXiuPlugin.getInstance().getRoundManager();
            boolean success = rm.placeBet(player, isTai(), amount);

            if (success) {
                EconomyUtil.withdraw((OfflinePlayer) player, amount);

                String money = MoneyUtils.formatVietMoney(amount);
                String side = isTai() ? "§bTài" : "§fXỉu";
                player.sendMessage("§aĐặt cược thành công " + money + "đ vào " + side + ".");

                Location loc = player.getLocation();
                player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }

            return true;
        }

        sender.sendMessage("Chỉ người chơi mới dùng lệnh này.");
        return true;
    }
}
