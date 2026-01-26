package me.tuanang.tuanangplugin.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MoneyUtils {

    public MoneyUtils() {
    }

    public static String formatVietMoney(double amount) {

        // >= 100 triệu
        if (amount >= 1.0E8) {
            double ty = amount / 1.0E9;

            DecimalFormat format = new DecimalFormat(
                    "#,###.##",
                    new DecimalFormatSymbols(new Locale("vi", "VN"))
            );

            return format.format(ty) + " Tỷ";
        }

        // < 100 triệu
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');

        DecimalFormat format = new DecimalFormat("#,###", symbols);
        return format.format(amount);
    }
}
