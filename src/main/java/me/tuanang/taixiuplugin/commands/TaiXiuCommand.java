package me.tuanang.taixiuplugin.commands;

import me.tuanang.taixiuplugin.gui.TaiXiuGUI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TaiXiuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;

        TaiXiuGUI.open(p);
        return true;
    }
}
