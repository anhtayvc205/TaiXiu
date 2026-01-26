package me.tuanang.tuanangplugin.data;

import me.tuanang.tuanangplugin.TaiXiuPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DataFile {

    private FileConfiguration config;
    private final File file;
    private final TaiXiuPlugin plugin;

    public DataFile() {
        this.plugin = TaiXiuPlugin.getInstance();

        this.file = new File(plugin.getDataFolder(), "data.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                this.config = YamlConfiguration.loadConfiguration(file);

                config.set("jackpot", 0.0D);
                config.set("history", new ArrayList<>());
                save();
            } catch (IOException e) {
                Logger logger = plugin.getLogger();
                logger.severe("Không thể tạo file data.yml: " + e.getMessage());
            }
        } else {
            this.config = YamlConfiguration.loadConfiguration(file);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Logger logger = plugin.getLogger();
            logger.severe("Không thể lưu data.yml: " + e.getMessage());
        }
    }
}
