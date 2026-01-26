package me.tuanang.tuanangplugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

class RoundManager$1 extends BukkitRunnable {

    int initCountdown;
    final RoundManager this$0;

    RoundManager$1(RoundManager manager) {
        this.this$0 = manager;
        this.initCountdown = manager.roundTime;
    }

    @Override
    public void run() {

        Bukkit.getOnlinePlayers().forEach(
                Objects.requireNonNull(this$0.bossBar)::addPlayer
        );

        if (initCountdown <= 0) {

            this.cancel();

            this$0.timeLeft = this$0.roundTime;
            this$0.updateBossBar(this$0.timeLeft);
            this$0.runGameRounds();

            return;
        }

        BossBar bar = this$0.bossBar;

        bar.setTitle("§eKhởi tạo... Bắt đầu sau §c" + initCountdown + "s");
        bar.setProgress(initCountdown / (double) this$0.roundTime);

        initCountdown--;
    }
}
