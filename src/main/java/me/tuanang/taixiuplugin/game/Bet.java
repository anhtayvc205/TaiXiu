package me.tuanang.taixiuplugin.game;

import java.util.UUID;

public class Bet {

    private final UUID player;
    private final long amount;
    private final BetType type;

    public Bet(UUID player, long amount, BetType type) {
        this.player = player;
        this.amount = amount;
        this.type = type;
    }

    public UUID getPlayer() {
        return player;
    }

    public long getAmount() {
        return amount;
    }

    public BetType getType() {
        return type;
    }
}
