package me.taixiu.data;

import me.taixiu.TaiXiuPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {

    private final File folder;
    private final HashMap<UUID, PlayerData> cache = new HashMap<>();

    public DataManager() {
        folder = new File(TaiXiuPlugin.instance.getDataFolder(), "playerdata");
        if (!folder.exists()) folder.mkdirs();
    }

    public PlayerData get(Player p) {
        return cache.computeIfAbsent(p.getUniqueId(), this::load);
    }

    private PlayerData load(UUID uuid) {
        File f = new File(folder, uuid.toString() + ".yml");
        PlayerData d = new PlayerData(uuid);

        if (!f.exists()) return d;

        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        d.addWin(c.getInt("wins"));
        d.addLose(c.getInt("loses"));
        d.addLose(0); // chỉ để init totalBet/profit

        return d;
    }

    public void save(PlayerData d) {
        File f = new File(folder, d.getUuid().toString() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        c.set("wins", d.getWins());
        c.set("loses", d.getLoses());
        c.set("totalBet", d.getTotalBet());
        c.set("profit", d.getProfit());

        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        cache.values().forEach(this::save);
    }
}
