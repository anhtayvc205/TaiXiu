package org.kazamistudio.taiXiuPlugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

class RoundManager$2 extends BukkitRunnable {

    final RoundManager this$0;

    RoundManager$2(RoundManager manager) {
        this.this$0 = manager;
    }

    @Override
    public void run() {

        Bukkit.getOnlinePlayers().forEach(
                Objects.requireNonNull(this$0.bossBar)::addPlayer
        );

        if (this$0.timeLeft <= 0) {

            this.cancel();
            this$0.processRoundWithAnimation();
            return;
        }

        this$0.updateBossBar(this$0.timeLeft);
        this$0.timeLeft--;
    }
}
