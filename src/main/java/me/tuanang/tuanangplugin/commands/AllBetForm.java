package me.tuanang.tuanangplugin.commands;

import me.tuanang.tuanangplugin.TuanAngPlugin;
import me.tuanang.tuanangplugin.managers.RoundManager;
import me.tuanang.tuanangplugin.utils.EconomyUtil;
import me.tuanang.tuanangplugin.utils.MoneyUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.cumulus.form.*;
import org.geysermc.cumulus.response.*;

import java.io.File;
import java.util.UUID;

public class AllBetForm {

    public AllBetForm() {
        super();
    }

    private static void placeBet(Player player, boolean tai, double money) {
        RoundManager rm = TaiXiuPlugin.getInstance().getRoundManager();

        boolean success = rm.placeBet(player, tai, money);
        if (success) {
            EconomyUtil.withdraw((OfflinePlayer) player, money);

            String formatted = MoneyUtils.formatVietMoney(money);
            String side = tai ? "§bTài" : "§fXỉu";
            player.sendMessage("§aĐặt cược thành công toàn bộ " + formatted + "đ vào " + side + ".");
        }
    }

    private static void openConfirmForm(Player player, FloodgatePlayer fgPlayer, boolean tai, double money) {
        String side = tai ? "§bTài" : "§fXỉu";

        ModalForm form = ModalForm.builder()
                .title("§eXác nhận cược")
                .content("Bạn có chắc muốn cược toàn bộ §a" + MoneyUtils.formatVietMoney(money) + "§fđ vào " + side + " không?")
                .button1("§aXác nhận")
                .button2("§cHuỷ")
                .validResultHandler((f, res) -> {
                    if (res.getResult()) {
                        Bukkit.getScheduler().runTask(
                                TuanAngPlugin.getInstance(),
                                () -> placeBet(player, tai, money)
                        );
                    } else {
                        player.sendMessage("§7Bạn đã huỷ cược.");
                    }
                })
                .build();

        fgPlayer.sendForm(form);
    }

    public static void send(Player player) {
        FloodgatePlayer fg = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (fg == null) return;

        double balance = EconomyUtil.getBalance((OfflinePlayer) player);
        if (balance <= 0) {
            player.sendMessage("§cBạn không có tiền để cược.");
            return;
        }

        SimpleForm form = SimpleForm.builder()
                .title("§6Cược tất cả tiền")
                .content("Bạn muốn cược tất cả §a" + MoneyUtils.formatVietMoney(balance) + "§fđ vào đâu?")
                .button("§bTài")
                .button("§fXỉu")
                .validResultHandler((f, res) -> {
                    boolean tai = res.getClickedButtonId() == 0;
                    openConfirmForm(player, fg, tai, balance);
                })
                .build();

        fg.sendForm(form);
    }
}
