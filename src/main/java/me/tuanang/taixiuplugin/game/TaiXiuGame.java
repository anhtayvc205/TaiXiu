package me.tuanang.taixiuplugin.game;

import me.tuanang.taixiuplugin.*;
import me.tuanang.taixiuplugin.bossbar.BossBarUtil;
import me.tuanang.taixiuplugin.animation.DiceAnimation;
import me.tuanang.taixiuplugin.discord.DiscordUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TaiXiuGame {

    private final TaiXiuPlugin plugin;
    private final BossBarUtil bar;
    private final Map<UUID, Bet> bets = new HashMap<>();
    private final List<String> history = new ArrayList<>();

    private double taiTotal = 0, xiuTotal = 0;
    private double jackpot;
    private int round = 1;
    private int time;

    public TaiXiuGame(TaiXiuPlugin plugin) {
        this.plugin = plugin;
        this.time = plugin.getConfig().getInt("round-time");
        this.jackpot = plugin.getConfig().getDouble("jackpot.start");
        this.bar = new BossBarUtil(plugin);
    }

    public boolean check(Player p, double money) {
        double min = plugin.getConfig().getDouble("bet.min");
        double max = plugin.getConfig().getDouble("bet.max");

        if (money < min || money > max) {
            p.sendMessage("§cCược: " + min + " - " + max);
            return false;
        }
        if (bets.containsKey(p.getUniqueId())) {
            p.sendMessage("§cBạn đã cược rồi!");
            return false;
        }
        return true;
    }

    public void bet(Player p, String type, double money) {
        if (type.equals("TAI")) taiTotal += money;
        else xiuTotal += money;

        jackpot += money * plugin.getConfig().getDouble("jackpot.tax");
        bets.put(p.getUniqueId(), new Bet(type, money));
    }

    public void start() {
        new BukkitRunnable() {
            int t = time;
            public void run() {
                if (t-- <= 0) {
                    cancel();
                    DiceAnimation.play(plugin, () -> endRound());
                    return;
                }
                bar.update(t, round, jackpot, taiTotal, xiuTotal);
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void endRound() {
        int sum = new Random().nextInt(16) + 3;
        String rs = sum >= 11 ? "TAI" : "XIU";

        history.add(rs);
        if (history.size() > 30) history.remove(0);

        Bukkit.broadcastMessage("§6Kết quả: §e" + rs);

        for (UUID id : bets.keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p == null) continue;
            Bet b = bets.get(id);
            if (b.type.equals(rs)) {
                plugin.currency().deposit(p, b.money * 2);
            }
        }

        if (plugin.getConfig().getBoolean("discord.enable")) {
            DiscordUtil.send(plugin, round, rs, jackpot);
        }

        bets.clear();
        taiTotal = 0;
        xiuTotal = 0;
        round++;
        start();
    }
}
