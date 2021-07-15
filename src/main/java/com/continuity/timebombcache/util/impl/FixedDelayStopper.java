package com.continuity.timebombcache.util.impl;

import com.continuity.timebombcache.util.Stopper;

import java.util.concurrent.TimeUnit;

public class FixedDelayStopper implements Stopper {

    private final int delayInSeconds;

    public FixedDelayStopper(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    @Override
    public void delay() {
        try {
            TimeUnit.SECONDS.sleep(delayInSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
