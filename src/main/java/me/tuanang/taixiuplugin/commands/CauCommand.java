package me.tuanang.taixiuplugin.commands;

import me.tuanang.taixiuplugin.TaiXiuPlugin;
import me.tuanang.taixiuplugin.gui.CauGUI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CauCommand implements CommandExecutor {

    private final TaiXiuPlugin plugin;

    public CauCommand(TaiXiuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;

        CauGUI.open(p, plugin.game().history);
        return true;
    }
}
