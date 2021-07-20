package com.continuity.timebombcache.util.impl;

import com.continuity.timebombcache.model.IntRange;
import com.continuity.timebombcache.util.Stopper;

import java.util.concurrent.TimeUnit;

public class RandomDelayStopper implements Stopper {

    private final IntRange range;

    public RandomDelayStopper(int minDelayInSeconds, int maxDelayInSeconds) {
        this.range = new IntRange(minDelayInSeconds, maxDelayInSeconds);
    }

    @Override
    public void delay() {
        try {
            TimeUnit.SECONDS.sleep(range.nextRandom());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
