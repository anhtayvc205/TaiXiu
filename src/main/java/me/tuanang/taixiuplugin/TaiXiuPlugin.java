package me.tuanang.taixiuplugin;

import me.tuanang.taixiuplugin.commands.TaiXiuAdminCommand;
import me.tuanang.taixiuplugin.currency.CurrencyManager;
import me.tuanang.taixiuplugin.game.TaiXiuGame;
import me.tuanang.taixiuplugin.util.DataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TaiXiuPlugin extends JavaPlugin {

    private static TaiXiuPlugin instance;
    private TaiXiuGame game;
    private DataManager data;
    private CurrencyManager currency;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("data.yml", false);

        data = new DataManager(this);
        data.load();

        currency = new CurrencyManager(this);
        game = new TaiXiuGame(this);

        getCommand("taixiuadmin").setExecutor(new TaiXiuAdminCommand(this));

        game.start();
        getLogger().info("TaiXiu ENABLED");
    }

    @Override
    public void onDisable() {
        game.stop();
        data.saveSync();
    }

    public static TaiXiuPlugin get() { return instance; }
    public TaiXiuGame getGame() { return game; }
    public DataManager getData() { return data; }
    public CurrencyManager getCurrency() { return currency; }
}
