package me.tuanang.taixiuplugin.discord;

import me.tuanang.taixiuplugin.TaiXiuPlugin;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DiscordUtil {

    public static void send(TaiXiuPlugin p, int round, String rs, double jackpot) {
        try {
            String json = """
            {
              "content": "<@&%s>",
              "embeds":[{
                "title":"🎲 TÀI XỈU",
                "fields":[
                  {"name":"Phiên","value":"#%d","inline":true},
                  {"name":"Kết quả","value":"%s","inline":true},
                  {"name":"Hũ","value":"%,.0f","inline":true}
                ]
              }]
            }
            """.formatted(
                p.getConfig().getString("discord.role-id"),
                round, rs, jackpot
            );

            HttpURLConnection con =
                (HttpURLConnection) new URL(p.getConfig().getString("discord.webhook-url")).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {}
    }
}
