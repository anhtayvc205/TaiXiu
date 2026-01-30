package me.taixiu.data;

import java.util.*;
import java.util.stream.Collectors;

public class Leaderboard {

    public static List<PlayerData> topWins(Collection<PlayerData> all) {
        return all.stream()
                .sorted((a, b) -> Integer.compare(b.getWins(), a.getWins()))
                .limit(10)
                .collect(Collectors.toList());
    }
}
