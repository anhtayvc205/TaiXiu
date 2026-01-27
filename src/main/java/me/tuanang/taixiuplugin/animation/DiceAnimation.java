package me.tuanang.taixiuplugin.animation;

import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Random;
import org.bukkit.plugin.Plugin;

public class DiceAnimation {

    public static void play(Plugin plugin, Runnable end) {
        new BukkitRunnable() {
            int i = 0;
            Random r = new Random();

            public void run() {
                if (i++ >= 20) {
                    cancel();
                    end.run();
                    return;
                }
                Bukkit.broadcastMessage("§e🎲 " +
                        (r.nextInt(6)+1)+"-"+(r.nextInt(6)+1)+"-"+(r.nextInt(6)+1));
            }
        }.runTaskTimer(plugin, 0, 3);
    }
}
