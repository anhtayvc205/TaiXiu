package me.tuanang.tuanangplugin;

import me.tuanang.tuanangplugin.commands.*;
import me.tuanang.tuanangplugin.data.DataFile;
import me.tuanang.tuanangplugin.listener.*;
import me.tuanang.tuanangplugin.managers.RoundManager;
import me.tuanang.tuanangplugin.utils.EconomyUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class TuanAngPlugin extends JavaPlugin {

    private static TuanAngPlugin instance;

    private DataFile dataFile;
    private FileConfiguration discordConfig;
    private RoundManager roundManager;

    public TuanAngPlugin() {
        super();
    }

    public static TuanAngPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!EconomyUtil.setup()) {
            Logger logger = getLogger();
            logger.severe("Không tìm thấy plugin Vault hoặc PlayerPoints, TokenManager. Plugin sẽ dừng.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        saveResource("discord_taixiu.yml", false);
        File discordFile = new File(getDataFolder(), "discord_taixiu.yml");
        discordConfig = YamlConfiguration.loadConfiguration(discordFile);

        dataFile = new DataFile();

        getCommand("tai").setExecutor(new TaiCommand());
        getCommand("xiu").setExecutor(new XiuCommand());
        getCommand("tai").setTabCompleter(new BetTabCompleter());
        getCommand("xiu").setTabCompleter(new BetTabCompleter());
        getCommand("cau").setExecutor(new CauCommand());
        getCommand("taixiu").setExecutor(new TaixiuMenuCommand());

        TaiXiuCommand adminCmd = new TaiXiuCommand();
        getCommand("taixiuadmin").setExecutor(adminCmd);
        getCommand("taixiuadmin").setTabCompleter(adminCmd);

        getCommand("all").setExecutor(new AllCommand());
        getCommand("allmenu").setExecutor(new AllMenuCommand());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new GUIListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);

        roundManager = new RoundManager();
        roundManager.loadData(dataFile);
        roundManager.startGameLoop();

        File guiDir = new File(getDataFolder(), "guid");
        if (!guiDir.exists()) {
            guiDir.mkdirs();
        }

        String[] guiFiles = {"taixiugui.yml", "taixiu_all.yml"};
        for (String f : guiFiles) {
            File file = new File(guiDir, f);
            if (!file.exists()) {
                saveResource("guid/" + f, false);
            }
        }

        logStartupMessage(true);
    }

    @Override
    public void onDisable() {
        if (roundManager != null && dataFile != null) {
            roundManager.saveData(dataFile);
        }
        logStartupMessage(false);
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public FileConfiguration getDiscordConfig() {
        return discordConfig;
    }

    public RoundManager getRoundManager() {
        return roundManager;
    }

    private void logStartupMessage(boolean enable) {
        if (enable) {
            logWithColor("&b========== &fTuanAng &b==========");
            logWithColor("&7[&a✔&7] &fPlugin: &b" + getDescription().getName());
            logWithColor("&7[&a✔&7] &fVersion: &a" + getDescription().getVersion());
            logWithColor("&7[&a✔&7] &fAuthor: &eTuanAng");
            logWithColor("&7[&a✔&7] &fDiscord: &9https://discord.gg/vQmPYHsCP");
            logWithColor("&7[&a✔&7] &6" + getDescription().getName() + " &ađã được bật!");
            logWithColor("&b=====================================");
            logWithColor("");
        } else {
            logWithColor("&c[&6" + getDescription().getName() + "&c] Plugin đã tắt.");
        }
    }

    private void logWithColor(String msg) {
        Server server = getServer();
        ConsoleCommandSender sender = server.getConsoleSender();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
