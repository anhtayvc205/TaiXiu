package me.tuanang.taixiuplugin.animation;

import java.util.concurrent.ThreadLocalRandom;

public class DiceAnimation {
    public static int roll() {
        return ThreadLocalRandom.current().nextInt(1, 7);
    }
}
