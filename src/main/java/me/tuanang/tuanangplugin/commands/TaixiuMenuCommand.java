package me.tuanang.tuanangplugin.commands;

import me.tuanang.tuanangplugin.commands.BetForm;
import me.tuanang.tuanangplugin.gui.TaixiuGUI;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class TaixiuMenuCommand implements CommandExecutor {

    public TaixiuMenuCommand() {
        super();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            PluginManager pm = Bukkit.getPluginManager();
            if (pm.getPlugin("floodgate") != null) {

                FloodgatePlayer fg = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                if (fg != null) {
                    BetForm.send(player);
                } else {
                    TaixiuGUI.openMainMenu(player);
                }

            } else {
                TaixiuGUI.openMainMenu(player);
            }

            return true;
        }

        sender.sendMessage("Chỉ dành cho người chơi.");
        return true;
    }
}
