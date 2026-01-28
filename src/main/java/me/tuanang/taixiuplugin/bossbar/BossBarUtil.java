package me.tuanang.taixiuplugin.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.*;

public class BossBarUtil {
    private static BossBar bar;

    public static void update(int time, int max) {
        if (bar == null)
            bar = Bukkit.createBossBar("§eTài Xỉu", BarColor.GREEN, BarStyle.SEGMENTED_10);

        double progress = Math.max(0, (double) time / max);
        bar.setProgress(progress);
        bar.setTitle("§eCòn " + time + "s để đặt cược");

        Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
    }
}
