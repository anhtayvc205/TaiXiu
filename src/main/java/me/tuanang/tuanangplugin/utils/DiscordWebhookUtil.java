package org.kazamistudio.taiXiuPlugin.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.kazamistudio.taiXiuPlugin.TaiXiuPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordWebhookUtil {

    public DiscordWebhookUtil() {
    }

    public static void sendWebhook(String title, String description, String thumbnailUrl, String footer, int color) {
        try {
            TaiXiuPlugin plugin = TaiXiuPlugin.getInstance();
            FileConfiguration config = plugin.getConfig();

            boolean enabled = config.getBoolean("discord.enabled", false);
            if (!enabled) {
                return;
            }

            String webhookUrl = config.getString("discord.webhook-url", "");
            if (webhookUrl.isEmpty()) {
                return;
            }

            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject embed = new JsonObject();
            embed.addProperty("title", title);
            embed.addProperty("description", description);
            embed.addProperty("color", color);

            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                JsonObject thumbnail = new JsonObject();
                thumbnail.addProperty("url", thumbnailUrl);
                embed.add("thumbnail", thumbnail);
            }

            if (footer != null && !footer.isEmpty()) {
                JsonObject footerObj = new JsonObject();
                footerObj.addProperty("text", footer);
                embed.add("footer", footerObj);
            }

            JsonArray embeds = new JsonArray();
            embeds.add(embed);

            JsonObject payload = new JsonObject();
            payload.add("embeds", embeds);

            OutputStream os = connection.getOutputStream();
            os.write(payload.toString().getBytes());
            os.flush();
            os.close();

            connection.getResponseCode(); // trigger request
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
