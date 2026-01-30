package me.taixiu.data;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private int wins;
    private int loses;
    private double totalBet;
    private double profit;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.wins = 0;
        this.loses = 0;
        this.totalBet = 0;
        this.profit = 0;
    }

    public UUID getUuid() { return uuid; }
    public int getWins() { return wins; }
    public int getLoses() { return loses; }
    public double getTotalBet() { return totalBet; }
    public double getProfit() { return profit; }

    public void addWin(double amount) {
        wins++;
        totalBet += amount;
        profit += amount;
    }

    public void addLose(double amount) {
        loses++;
        totalBet += amount;
        profit -= amount;
    }
      }
