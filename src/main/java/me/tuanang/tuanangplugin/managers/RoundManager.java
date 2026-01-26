package me.tuanang.tuanangplugin.managers;

import me.tuanang.tuanangplugin.TaiXiuPlugin;
import me.tuanang.tuanangplugin.utils.DiscordWebhookUtil;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class RoundManager {

    public static final double MAX_BET = 1_000_000.0;
    public static final double MIN_BET = 1_000.0;

    private final Map<UUID, BetData> currentBets;
    private final Map<Boolean, Double> totalBets;
    private final Map<Boolean, List<UUID>> betPlayers;
    private final Map<UUID, Long> lastBetAttempt;
    private final LinkedList<Result> history;
    private final LinkedList<RoundHistory> roundHistories;

    private final BossBar bossBar;
    private final int[] currentDice;

    private final int roundTime;
    private int timeLeft;
    private int roundNumber;

    private double jackpot;
    private final double houseWinChance;
    private final double jackpotExplodeChance;
    private final double[] jackpotRates;

    public RoundManager() {

        this.roundNumber = 1;
        this.jackpot = 0D;

        this.currentBets = new HashMap<>();
        this.totalBets = new HashMap<>();
        this.betPlayers = new HashMap<>();
        this.lastBetAttempt = new HashMap<>();
        this.history = new LinkedList<>();
        this.roundHistories = new LinkedList<>();
        this.currentDice = new int[3];

        totalBets.put(true, 0D);
        totalBets.put(false, 0D);

        betPlayers.put(true, new ArrayList<>());
        betPlayers.put(false, new ArrayList<>());

        FileConfiguration config = TaiXiuPlugin.getInstance().getConfig();

        this.roundTime = config.getInt("round-time", 60);
        this.houseWinChance = config.getDouble("house-win-chance", 0.1D);
        this.jackpotExplodeChance = config.getDouble("jackpot-explode-chance", 0.3D);

        double top1 = config.getDouble("jackpot-share.top1", 60.0) / 100.0;
        double top2 = config.getDouble("jackpot-share.top2", 25.0) / 100.0;
        double top3 = config.getDouble("jackpot-share.top3", 15.0) / 100.0;

        this.jackpotRates = new double[]{top1, top2, top3};

        this.bossBar = Bukkit.createBossBar(
                "§eĐang khởi tạo phiên...",
                BarColor.PURPLE,
                BarStyle.SEGMENTED_10
        );

        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
    }

    /* ==========================
       finalizeRound (PHẦN ĐÃ GỬI)
       ========================== */
    private void finalizeRound(int[] dice) {

        boolean actionBar = TaiXiuPlugin.getInstance()
                .getConfig()
                .getBoolean("display.actionbar", true);

        for (int i = 0; i < currentDice.length; i++) {
            currentDice[i] = dice[i];
        }

        int sum = dice[0] + dice[1] + dice[2];
        Result result = (sum >= 11 ? Result.TAI : Result.XIU);
        boolean isTai = (result == Result.TAI);

        if (actionBar) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.spigot().sendMessage(
                        net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        new net.md_5.bungee.api.chat.TextComponent(
                                "§a🎲 " + getDiceVisual() + " = " + sum + " → " + (isTai ? "§bTài" : "§fXỉu")
                        )
                );
            }
        }

        FileConfiguration discord = TaiXiuPlugin.getInstance().getDiscordConfig();
        FileConfiguration config = TaiXiuPlugin.getInstance().getConfig();

        String roleId = config.getString("discord.role-id", "");

        String title = discord.getString("result-message.title", "")
                .replace("%round%", String.valueOf(roundNumber));

        String desc = discord.getString("result-message.description", "")
                .replace("%dice%", getDiceVisual())
                .replace("%side%", isTai ? "Tài" : "Xỉu")
                .replace("%role_id%", roleId);

        int color = discord.getInt("result-message.color", 0xe67e22);

        String thumb = isTai
                ? discord.getString("result-message.thumbnail-tai", "")
                : discord.getString("result-message.thumbnail-xiu", "");

        String footer = discord.getString("result-message.footer", "");

        DiscordWebhookUtil.sendWebhook(title, desc, thumb, footer, color);

        Bukkit.broadcastMessage(
                "§6[Phiên #" + roundNumber + "] Kết quả: " +
                        getDiceVisual() + " = " + sum + " → " + (isTai ? "§bTài" : "§fXỉu")
        );

        boolean houseWin = Math.random() < houseWinChance;

        if (houseWin) {
            Bukkit.broadcastMessage("§4[ NHÀ CÁI THẮNG ] §cNhà cái đã ăn toàn bộ tiền cược ở phiên này!");
        }
    }

    /* ====== CÁC METHOD KHÁC BẠN CHƯA GỬI ====== */
    private String getDiceVisual() {
        return "";
    }

    /* ====== INNER CLASSES / ENUM (chưa gửi) ====== */
    public enum Result {
        TAI, XIU
    }

    public static class BetData {}
    public static class RoundHistory {}
}
