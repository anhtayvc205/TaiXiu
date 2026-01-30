package me.taixiu;

import me.taixiu.data.DataManager;
import me.taixiu.gui.TaiXiuGUI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TaiXiuPlugin extends JavaPlugin {

    public static Economy econ;
    public static TaiXiuPlugin instance;
    public DataManager data;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupEconomy();
        data = new DataManager();
        Bukkit.getPluginManager().registerEvents(new TaiXiuGUI(), this);
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;
        TaiXiuGUI.open(p);
        return true;
    }
}
