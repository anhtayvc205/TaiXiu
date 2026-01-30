package me.taixiu.game;

import me.taixiu.TaiXiuPlugin;
import me.taixiu.data.PlayerData;
import me.taixiu.utils.CooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TaiXiuGame {

    private static final Random r = new Random();

    public static void play(Player p, String choice) {

        // anti spam + anti double play
        if (CooldownManager.onCooldown(p)) {
            p.sendMessage("§c⏳ Vui lòng chờ cooldown!");
            return;
        }

        double bet = TaiXiuPlugin.instance.getConfig().getDouble("bet.min");

        if (TaiXiuPlugin.econ.getBalance(p) < bet) {
            p.sendMessage("§c💰 Không đủ tiền!");
            return;
        }

        TaiXiuPlugin.econ.withdrawPlayer(p, bet);
        CooldownManager.set(p);

        p.sendMessage("§e🎲 Đang lắc xúc xắc...");

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {

                if (!p.isOnline()) {
                    cancel();
                    return;
                }

                // sound an toàn cho 1.21.x
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

                tick++;
                if (tick >= 10) {
                    cancel();
                    endGame(p, choice, bet);
                }
            }
        }.runTaskTimer(TaiXiuPlugin.instance, 0, 4);
    }

    private static void endGame(Player p, String choice, double bet) {

        int d1 = r.nextInt(6) + 1;
        int d2 = r.nextInt(6) + 1;
        int d3 = r.nextInt(6) + 1;

        int total = d1 + d2 + d3;

        // ===== RATE CONFIG =====
        int rateTai = TaiXiuPlugin.instance.getConfig().getInt("rate.tai");
        int roll = r.nextInt(100) + 1;

        String result;
        if (roll <= rateTai) {
            result = "tai";
        } else {
            result = "xiu";
        }

        p.sendMessage("§7Xúc xắc: §f" + d1 + " §f" + d2 + " §f" + d3);
        p.sendMessage("§eTổng: §a" + total + " → §6" + result.toUpperCase());

        PlayerData data = TaiXiuPlugin.instance.data.get(p);

        if (choice.equals(result)) {
            TaiXiuPlugin.econ.depositPlayer(p, bet * 2);
            p.sendMessage("§a✅ Bạn THẮNG!");

            data.addWin(bet);
        } else {
            p.sendMessage("§c❌ Bạn THUA!");

            data.addLose(bet);
        }

        TaiXiuPlugin.instance.data.save(data);
    }
}
