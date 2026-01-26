package me.tuanang.tuanangplugin.utils;

import me.realized.tokenmanager.api.TokenManager;
import me.tuanang.tuanangplugin.TuanAngPlugin;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;

public class EconomyUtil {

    private static PlayerPointsAPI playerPointsAPI;
    private static TokenManager tokenManager;
    private static Economy vaultEcon;
    private static String type = "vault"; // default an toàn

    /* ===================== SETUP ===================== */

    public static boolean setup() {
        FileConfiguration config = TuanAngPlugin.getInstance().getConfig();
        type = config.getString("currency.type", "vault").toLowerCase();

        List<String> supported = Arrays.asList("vault", "playerpoints", "tokenmanager");

        if (!supported.contains(type)) {
            Bukkit.getLogger().warning("❌ Loại tiền tệ không hợp lệ: " + type);
            return tryFallback(supported);
        }

        if (!initType(type)) {
            Bukkit.getLogger().warning("❌ Không tìm thấy plugin cho loại tiền: " + type);
            return tryFallback(supported);
        }

        return true;
    }

    private static boolean tryFallback(List<String> list) {
        for (String t : list) {
            if (initType(t)) {
                Bukkit.getLogger().info("⚠ Tự động chuyển sang loại tiền: " + t);
                type = t;
                TuanAngPlugin.getInstance().getConfig().set("currency.type", t);
                TuanAngPlugin.getInstance().saveConfig();
                return true;
            }
        }

        Bukkit.getLogger().severe("❌ Không tìm thấy plugin tiền tệ nào!");
        return false;
    }

    private static boolean initType(String t) {
        switch (t) {
            case "vault":
                Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
                if (vault != null) {
                    RegisteredServiceProvider<Economy> rsp =
                            Bukkit.getServicesManager().getRegistration(Economy.class);
                    if (rsp != null) {
                        vaultEcon = rsp.getProvider();
                        return vaultEcon != null;
                    }
                }
                return false;

            case "playerpoints":
                Plugin pp = Bukkit.getPluginManager().getPlugin("PlayerPoints");
                if (pp != null) {
                    playerPointsAPI = PlayerPoints.getInstance().getAPI();
                    return playerPointsAPI != null;
                }
                return false;

            case "tokenmanager":
                Plugin tm = Bukkit.getPluginManager().getPlugin("TokenManager");
                if (tm != null) {
                    RegisteredServiceProvider<TokenManager> rsp =
                            Bukkit.getServicesManager().getRegistration(TokenManager.class);
                    if (rsp != null) {
                        tokenManager = rsp.getProvider();
                        return tokenManager != null;
                    }
                }
                return false;
        }
        return false;
    }

    /* ===================== BALANCE ===================== */

    public static double getBalance(OfflinePlayer player) {
        switch (type) {
            case "vault":
                return vaultEcon.getBalance(player);

            case "playerpoints":
                return playerPointsAPI.look(player.getUniqueId());

            case "tokenmanager":
                if (!player.isOnline()) return 0;
                return tokenManager.getTokens(player.getPlayer()).orElse(0);

            default:
                return 0;
        }
    }

    /* ===================== WITHDRAW ===================== */

    public static boolean withdraw(OfflinePlayer player, double amount) {
        switch (type) {
            case "vault":
                return vaultEcon.withdrawPlayer(player, amount).transactionSuccess();

            case "playerpoints":
                return playerPointsAPI.take(player.getUniqueId(), (int) amount);

            case "tokenmanager":
                if (!player.isOnline()) return false;
                return tokenManager.removeTokens(player.getPlayer(), (long) amount);

            default:
                return false;
        }
    }

    /* ===================== DEPOSIT ===================== */

    public static void deposit(OfflinePlayer player, double amount) {
        switch (type) {
            case "vault":
                vaultEcon.depositPlayer(player, amount);
                break;

            case "playerpoints":
                playerPointsAPI.give(player.getUniqueId(), (int) amount);
                break;

            case "tokenmanager":
                if (!player.isOnline()) return;
                tokenManager.addTokens(player.getPlayer(), (long) amount);
                break;
        }
    }
}
