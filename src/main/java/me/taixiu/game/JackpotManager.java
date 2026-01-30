package me.taixiu.game;

import me.taixiu.TaiXiuPlugin;

public class JackpotManager {

    private static double jackpot = 0;

    public static void add(double bet) {
        double percent = TaiXiuPlugin.instance.getConfig().getDouble("jackpot.percent");
        jackpot += bet * percent / 100.0;
    }

    public static double get() {
        return jackpot;
    }

    public static void win(org.bukkit.entity.Player p) {
        TaiXiuPlugin.econ.depositPlayer(p, jackpot);
        p.sendMessage("§6🎉 JACKPOT: +" + jackpot);
        jackpot = 0;
    }
}
