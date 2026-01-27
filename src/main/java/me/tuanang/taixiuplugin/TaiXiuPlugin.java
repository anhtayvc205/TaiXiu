package me.tuanang.taixiuplugin;

import me.tuanang.taixiuplugin.currency.CurrencyManager;
import me.tuanang.taixiuplugin.game.TaiXiuGame;
import me.tuanang.taixiuplugin.commands.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class TaiXiuPlugin extends JavaPlugin {

    private static TaiXiuPlugin inst;
    private static Economy econ;
    private TaiXiuGame game;
    private CurrencyManager currency;

    @Override
    public void onEnable() {
        inst = this;
        saveDefaultConfig();
        setupEconomy();

        currency = new CurrencyManager(this);
        game = new TaiXiuGame(this);
        game.start();

        getCommand("tai").setExecutor(new TaiCommand(this));
        getCommand("xiu").setExecutor(new XiuCommand(this));
        getCommand("all").setExecutor(new AllCommand(this));
        getCommand("allmenu").setExecutor(new AllMenuCommand());
        getCommand("cau").setExecutor(new CauCommand(this));
        getCommand("taixiu").setExecutor(new TaiXiuCommand());
        getCommand("taixiuadmin").setExecutor(new TaiXiuAdminCommand(this));
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp =
            getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = rsp.getProvider();
    }

    public TaiXiuGame game() { return game; }
    public CurrencyManager currency() { return currency; }
    public static Economy econ() { return econ; }
    public static TaiXiuPlugin get() { return inst; }
}
