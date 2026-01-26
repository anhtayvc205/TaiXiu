package me.tuanang.tuanangplugin.listener;

import me.tuanang.tuanangplugin.TuanAngPlugin;
import me.tuanang.tuanangplugin.managers.RoundManager;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener() {
        super();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        RoundManager roundManager = TuanAngPlugin.getInstance().getRoundManager();

        BossBar bossBar = roundManager.getBossBar();
        Player player = event.getPlayer();

        bossBar.addPlayer(player);
        roundManager.refreshBossBar();
    }
}
