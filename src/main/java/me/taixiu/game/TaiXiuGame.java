package me.taixiu.game;

import me.taixiu.TaiXiuPlugin;
import me.taixiu.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TaiXiuGame {

    static Random r = new Random();

    public static void play(Player p, String choice) {
        if (CooldownManager.onCooldown(p)) {
            p.sendMessage("§cChờ cooldown!");
            return;
        }

        double bet = TaiXiuPlugin.instance.getConfig().getDouble("bet.min");
        if (TaiXiuPlugin.econ.getBalance(p) < bet) {
            p.sendMessage("§cKhông đủ tiền!");
            return;
        }

        TaiXiuPlugin.econ.withdrawPlayer(p, bet);
        CooldownManager.set(p);

        p.sendMessage("§e🎲 Đang lắc xúc xắc...");

        new BukkitRunnable() {
            int t = 0;

            @Override
            public void run() {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                t++;
                if (t >= 10) {
                    cancel();
                    end(p, choice, bet);
                }
            }
        }.runTaskTimer(TaiXiuPlugin.instance, 0, 4);
    }

    private static void end(Player p, String choice, double bet) {
        int d1 = r.nextInt(6) + 1;
        int d2 = r.nextInt(6) + 1;
        int d3 = r.nextInt(6) + 1;

        int total = d1 + d2 + d3;
        String rs = total >= 11 ? "tai" : "xiu";

        p.sendMessage("§7Xúc xắc: " + d1 + " " + d2 + " " + d3);
        p.sendMessage("§eKết quả: §6" + rs.toUpperCase());

        if (choice.equals(rs)) {
            TaiXiuPlugin.econ.depositPlayer(p, bet * 2);
            p.sendMessage("§aBạn thắng!");
        } else {
            p.sendMessage("§cBạn thua!");
        }
    }
}
