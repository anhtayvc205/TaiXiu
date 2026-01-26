package me.tuanang.tuanangplugin.commands;

import me.tuanang.tuanangplugin.gui.CauGUI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CauCommand implements CommandExecutor {

    public CauCommand() {
        super();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            CauGUI.open(player);
            return true;
        }

        sender.sendMessage("Chỉ người chơi mới dùng lệnh này.");
        return true;
    }
}
