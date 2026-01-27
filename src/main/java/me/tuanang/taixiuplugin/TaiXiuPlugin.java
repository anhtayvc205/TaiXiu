package me.tuanang.taixiuplugin;

import me.tuanang.taixiuplugin.commands.*;
import me.tuanang.taixiuplugin.currency.CurrencyManager;
import me.tuanang.taixiuplugin.game.TaiXiuGame;
import me.tuanang.taixiuplugin.listeners.InventoryListener;
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

        // ===== FILES =====
        saveDefaultConfig();
        saveResource("data.yml", false);

        // ===== MANAGERS =====
        data = new DataManager(this);
        data.load();

        currency = new CurrencyManager(this);
        game = new TaiXiuGame(this);

        // ===== COMMANDS (ĐÚNG 100% plugin.yml) =====
        register("tai", new TaiCommand(this));
        register("xiu", new XiuCommand(this));
        register("all", new AllCommand(this));
        register("allmenu", new AllMenuCommand(this));
        register("cau", new CauCommand(this));
        register("taixiu", new TaiXiuCommand(this));
        register("taixiuadmin", new TaiXiuAdminCommand(this));

        // ===== LISTENERS =====
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        // ===== START GAME LOOP =====
        game.start();

        getLogger().info("§aTaiXiu ENABLED!");
    }

    @Override
    public void onDisable() {
        if (game != null) game.stop();
        if (data != null) data.saveSync();
        getLogger().info("§cTaiXiu DISABLED!");
    }

    // ===== SAFE REGISTER =====
    private void register(String name, Object executor) {
        if (getCommand(name) == null) {
            getLogger().severe("❌ Thiếu lệnh trong plugin.yml: " + name);
            return;
        }
        getCommand(name).setExecutor((org.bukkit.command.CommandExecutor) executor);
    }

    // ===== GETTERS =====
    public static TaiXiuPlugin get() {
        return instance;
    }

    public TaiXiuGame getGame() {
        return game;
    }

    public DataManager getData() {
        return data;
    }

    public CurrencyManager getCurrency() {
        return currency;
    }
}
