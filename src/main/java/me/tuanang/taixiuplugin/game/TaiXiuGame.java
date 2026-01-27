package me.tuanang.taixiuplugin.game;

import me.tuanang.taixiuplugin.TaiXiuPlugin;
import me.tuanang.taixiuplugin.animation.DiceAnimation;
import me.tuanang.taixiuplugin.bossbar.BossBarUtil;
import me.tuanang.taixiuplugin.discord.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TaiXiuGame {

    private final TaiXiuPlugin plugin;
    private BukkitTask task;

    private int time;
    private int cooldown;
    private int round = 0;

    private final Map<UUID, Bet> bets = new HashMap<>();

    public TaiXiuGame(TaiXiuPlugin plugin) {
        this.plugin = plugin;
        this.cooldown = plugin.getConfig().getInt("round.cooldown", 60);
        this.time = cooldown;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (time <= 0) {
                endRound();
                return;
            }
            BossBarUtil.update(time, cooldown);
            time--;
        }, 0L, 20L);
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    private void endRound() {
        stop();

        int d1 = DiceAnimation.roll();
        int d2 = DiceAnimation.roll();
        int d3 = DiceAnimation.roll();
        int total = d1 + d2 + d3;

        BetType result = total >= 11 ? BetType.TAI : BetType.XIU;

        Bukkit.broadcastMessage("§eKết quả: " + total + " → " + result.name());

        // trả thưởng
        bets.values().forEach(b -> {
            if (b.getType() == result) {
                plugin.getCurrency().give(b.getPlayer(), b.getAmount() * 2);
            }
        });

        plugin.getData().addHistory(result);
        DiscordUtil.sendResult(round, total, result);

        Bukkit.getScheduler().runTaskLater(plugin, this::reset, 60L);
    }

    private void reset() {
        bets.clear();
        round++;
        cooldown = plugin.getConfig().getInt("round.cooldown", 60);
        time = cooldown;
        start();
    }

    public void reloadCooldown() {
        cooldown = plugin.getConfig().getInt("round.cooldown", 60);
        if (time > cooldown) time = cooldown;
    }

    public void addBet(UUID uuid, Bet bet) {
        bets.put(uuid, bet);
    }
}
