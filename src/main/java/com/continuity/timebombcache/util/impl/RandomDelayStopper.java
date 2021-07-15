package com.continuity.timebombcache.util.impl;

import com.continuity.timebombcache.util.Stopper;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomDelayStopper implements Stopper {

    private final int minDelayInSeconds;
    private final int maxDelayInSeconds;
    private final Random random = new Random();

    public RandomDelayStopper(int minDelayInSeconds, int maxDelayInSeconds) {
        this.minDelayInSeconds = minDelayInSeconds;
        this.maxDelayInSeconds = maxDelayInSeconds;
    }

    @Override
    public void delay() {
        int sleepDelay = random.nextInt(maxDelayInSeconds - minDelayInSeconds) + minDelayInSeconds;
        try {
            TimeUnit.SECONDS.sleep(sleepDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
