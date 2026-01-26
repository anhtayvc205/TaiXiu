package org.kazamistudio.taiXiuPlugin.managers;

import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.kazamistudio.taiXiuPlugin.TaiXiuPlugin;
import org.kazamistudio.taiXiuPlugin.utils.DiscordWebhookUtil;

import java.util.*;

public class RoundManager {

    public static final double MAX_BET = 1_000_000D;
    public static final double MIN_BET = 1_000D;

    private final Map<UUID, BetData> currentBets = new HashMap<>();
    private final Map<Boolean, Double> totalBets = new HashMap<>();
    private final Map<Boolean, List<UUID>> betPlayers = new HashMap<>();
    private final Map<UUID, Long> lastBetAttempt = new HashMap<>();

    private final LinkedList<Result> history = new LinkedList<>();
    private final LinkedList<RoundHistory> roundHistories = new LinkedList<>();

    private final int[] currentDice = new int[3];
    private final BossBar bossBar;

    private final int roundTime;
    private int timeLeft;
    private int roundNumber = 1;

    private final double houseWinChance;
    private final double jackpotExplodeChance;
    private final double[] jackpotRates;
    private double jackpot = 0;

    public RoundManager() {

        totalBets.put(true, 0D);
        totalBets.put(false, 0D);

        betPlayers.put(true, new ArrayList<>());
        betPlayers.put(false, new ArrayList<>());

        FileConfiguration cfg = TaiXiuPlugin.getInstance().getConfig();

        roundTime = cfg.getInt("round-time", 60);
        houseWinChance = cfg.getDouble("house-win-chance", 0.1);
        jackpotExplodeChance = cfg.getDouble("jackpot-explode-chance", 0.3);

        jackpotRates = new double[]{
                cfg.getDouble("jackpot-share.top1", 60D) / 100D,
                cfg.getDouble("jackpot-share.top2", 25D) / 100D,
                cfg.getDouble("jackpot-share.top3", 15D) / 100D
        };

        bossBar = Bukkit.createBossBar(
                "§eĐang khởi tạo phiên...",
                BarColor.PURPLE,
                BarStyle.SEGMENTED_10
        );
        bossBar.setVisible(true);

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
    }

    /* ================= GAME LOOP ================= */

    public void startGameLoop() {
        new BukkitRunnable() {
            int initCountdown = roundTime;

            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

                if (initCountdown <= 0) {
                    cancel();
                    timeLeft = roundTime;
                    updateBossBar(timeLeft);
                    runGameRounds();
                    return;
                }

                bossBar.setTitle("§eKhởi tạo... Bắt đầu sau §c" + initCountdown + "s");
                bossBar.setProgress((double) initCountdown / roundTime);
                initCountdown--;
            }
        }.runTaskTimer(TaiXiuPlugin.getInstance(), 0, 20);
    }

    public void startNewRound() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

                if (timeLeft <= 0) {
                    cancel();
                    processRoundWithAnimation();
                    return;
                }

                updateBossBar(timeLeft);
                timeLeft--;
            }
        }.runTaskTimer(TaiXiuPlugin.getInstance(), 0, 20);
    }

    /* ================= ROUND ================= */

    private void finalizeRound(int[] dice) {
        System.arraycopy(dice, 0, currentDice, 0, 3);

        int sum = dice[0] + dice[1] + dice[2];
        Result result = sum >= 11 ? Result.TAI : Result.XIU;
        boolean isTai = result == Result.TAI;

        // broadcast
        Bukkit.broadcastMessage("§6[Phiên #" + roundNumber + "] Kết quả: "
                + getDiceVisual() + " = " + sum + " → " + (isTai ? "§bTài" : "§fXỉu"));

        // lưu lịch sử
        roundHistories.addFirst(new RoundHistory(result, dice, false));
        history.add(result);

        roundNumber++;
    }

    /* ================= UI ================= */

    public void updateBossBar(int time) {
        bossBar.setTitle("§eĐang cược... §c" + time + "s");
        bossBar.setProgress((double) time / roundTime);
    }

    private String getDiceVisual() {
        return "🎲 " + currentDice[0] + " - " + currentDice[1] + " - " + currentDice[2];
    }

    /* ================= INNER CLASSES ================= */

    public static class BetData {
        final double amount;
        final boolean isTai;

        BetData(double amount, boolean isTai) {
            this.amount = amount;
            this.isTai = isTai;
        }
    }

    public enum Result {
        TAI, XIU
    }

    public static class RoundHistory {
        public final Result result;
        public final int[] dice;
        public final boolean jackpot;

        public RoundHistory(Result result, int[] dice, boolean jackpot) {
            this.result = result;
            this.dice = dice.clone();
            this.jackpot = jackpot;
        }
    }
}
