package me.taixiu.utils;

import me.taixiu.TaiXiuPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownManager {
    static HashMap<Player, Long> cd = new HashMap<>();

    public static boolean onCooldown(Player p) {
        long time = TaiXiuPlugin.instance.getConfig().getInt("cooldown") * 1000L;
        return cd.containsKey(p) && System.currentTimeMillis() - cd.get(p) < time;
    }

    public static void set(Player p) {
        cd.put(p, System.currentTimeMillis());
    }
}
