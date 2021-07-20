package com.continuity.timebombcache.model;

import java.util.Random;

public class IntRange {

    private final int min;
    private final int max;
    private final Random random = new Random(System.currentTimeMillis());

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int nextRandom() {
        return random.nextInt(max - min) + min;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
