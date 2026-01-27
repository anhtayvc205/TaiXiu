package me.tuanang.taixiuplugin.commands;

import me.tuanang.taixiuplugin.gui.AllMenuGUI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AllMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;

        AllMenuGUI.open(p);
        return true;
    }
}
