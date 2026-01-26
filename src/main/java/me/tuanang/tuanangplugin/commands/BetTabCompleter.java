package me.tuanang.tuanangplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class BetTabCompleter implements TabCompleter {

    private static final List<String> SUGGESTIONS = Arrays.asList(
            "1000",
            "5000",
            "10000",
            "50000",
            "100000",
            "200000",
            "500000",
            "1000000"
    );

    public BetTabCompleter() {
        super();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return SUGGESTIONS.stream()
                    .filter(s -> s.startsWith(input))
                    .toList();
        }

        return Collections.emptyList();
    }
}
