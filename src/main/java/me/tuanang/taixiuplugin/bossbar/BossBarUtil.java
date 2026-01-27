package me.tuanang.taixiuplugin.bossbar;

import me.tuanang.taixiuplugin.TaiXiuPlugin;
import org.bukkit.*;
import org.bukkit.boss.*;

public class BossBarUtil {

    private final BossBar bar;
    private final TaiXiuPlugin plugin;

    public BossBarUtil(TaiXiuPlugin plugin) {
        this.plugin = plugin;
        bar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
        Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
    }

    public void update(int time, int round, double jackpot, double tai, double xiu) {
        String title = plugin.getConfig().getString("bossbar.title")
            .replace("%round%", round+"")
            .replace("%time%", time+"")
            .replace("%jackpot%", f(jackpot))
            .replace("%tai%", f(tai))
            .replace("%xiu%", f(xiu));

        bar.setTitle(ChatColor.translateAlternateColorCodes('&', title));

        int g = plugin.getConfig().getInt("bossbar.colors.green");
        int y = plugin.getConfig().getInt("bossbar.colors.yellow");

        bar.setColor(time > g ? BarColor.GREEN : time > y ? BarColor.YELLOW : BarColor.RED);
        bar.setProgress(Math.max(0, (double) time / plugin.getConfig().getInt("round-time")));
    }

    private String f(double d) { return String.format("%,.0f", d); }
}
