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

public class BetForm {

    public BetForm() {
        super();
    }

    private static void placeBet(Player player, boolean tai, double money) {
        RoundManager rm = TuanAngPlugin.getInstance().getRoundManager();

        boolean success = rm.placeBet(player, tai, money);
        if (success) {
            EconomyUtil.withdraw((OfflinePlayer) player, money);

            String formatted = MoneyUtils.formatVietMoney(money);
            String side = tai ? "§bTài" : "§fXỉu";
            player.sendMessage("§aĐặt cược thành công " + formatted + "đ vào " + side + ".");
        }
    }

    private static void openInputForm(Player player, FloodgatePlayer fg, boolean tai) {
        String side = tai ? "Tài" : "Xỉu";

        CustomForm form = CustomForm.builder()
                .title("§eNhập tiền cược cho " + side)
                .input("Số tiền muốn cược", "Ví dụ: 10000")
                .validResultHandler((f, res) -> {
                    throw new RuntimeException("Couldn't be decompiled.");
                })
                .build();

        fg.sendForm(form);
    }

    public static void send(Player player) {
        FloodgatePlayer fg = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (fg == null) return;

        SimpleForm form = SimpleForm.builder()
                .title("§6Cược Tài / Xỉu")
                .content("Chọn cửa bạn muốn đặt:")
                .button("§bTài")
                .button("§fXỉu")
                .validResultHandler((f, res) -> {
                    boolean tai = res.getClickedButtonId() == 0;
                    openInputForm(player, fg, tai);
                })
                .build();

        fg.sendForm(form);
    }
}
