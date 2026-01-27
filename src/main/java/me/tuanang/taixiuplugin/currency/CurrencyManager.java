package me.tuanang.taixiuplugin.currency;

import me.tuanang.taixiuplugin.TaiXiuPlugin;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class CurrencyManager {

    private final String type;
    private Economy vault;
    private PlayerPointsAPI pp;
    private TokenManager tm;

    public CurrencyManager(TaiXiuPlugin plugin) {
        this.type = plugin.getConfig().getString("currency.type").toLowerCase();
        if (type.equals("vault")) vault = TaiXiuPlugin.econ();
        if (type.equals("playerpoints")) pp = PlayerPointsAPI.getInstance();
        if (type.equals("tokenmanager"))
            tm = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
    }

    public boolean has(Player p, double m) {
        return switch (type) {
            case "playerpoints" -> pp.look(p.getUniqueId()) >= m;
            case "tokenmanager" -> tm.getTokens(p).orElse(0) >= m;
            default -> vault.has(p, m);
        };
    }

    public void withdraw(Player p, double m) {
        switch (type) {
            case "playerpoints" -> pp.take(p.getUniqueId(), (int)m);
            case "tokenmanager" -> tm.removeTokens(p, (int)m);
            default -> vault.withdrawPlayer(p, m);
        }
    }

    public void deposit(Player p, double m) {
        switch (type) {
            case "playerpoints" -> pp.give(p.getUniqueId(), (int)m);
            case "tokenmanager" -> tm.addTokens(p, (int)m);
            default -> vault.depositPlayer(p, m);
        }
    }
}
