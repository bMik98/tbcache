package com.continuity.timebombcache.service;

import com.continuity.timebombcache.model.IntRange;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractRandomlyCyclicService implements CyclicService {

    private final IntRange range;
    private final AtomicReference<ScheduledExecutorService> executorService = new AtomicReference<>();

    protected AbstractRandomlyCyclicService(int minDelayInSeconds, int maxDelayInSeconds) {
        range = new IntRange(minDelayInSeconds, maxDelayInSeconds);
    }

    @Override
    public void startCyclicTask() {
        if (executorService.compareAndSet(null, Executors.newSingleThreadScheduledExecutor())) {
            ScheduledExecutorService executor = executorService.get();
            if (executor != null) {
                executor.schedule(taskWrapper(task()), range.nextRandom(), TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void shutdown() {
        ExecutorService executor = executorService.getAndUpdate(es -> null);
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(range.getMax(), TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return executorService.get() != null;
    }

    private Runnable taskWrapper(Runnable runnable) {
        return () -> {
            runnable.run();
            ScheduledExecutorService executor = executorService.get();
            if (executor != null) {
                executor.schedule(taskWrapper(task()), range.nextRandom(), TimeUnit.SECONDS);
            }
        };
    }

    protected abstract Runnable task();
}
