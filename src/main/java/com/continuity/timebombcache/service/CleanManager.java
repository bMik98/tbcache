package com.continuity.timebombcache.service;

import com.continuity.timebombcache.model.Clearable;

import java.util.Collection;
import java.util.logging.Logger;

public class CleanManager extends AbstractRandomlyCyclicService {
    private static final Logger LOGGER = Logger.getLogger(CleanManager.class.getSimpleName());

    private final Collection<Clearable> caches;

    public CleanManager(Collection<Clearable> caches, int minDelayInSeconds, int maxDelayInSeconds) {
        super(minDelayInSeconds, maxDelayInSeconds);
        this.caches = caches;
    }

    @Override
    protected Runnable task() {
        LOGGER.info(() -> Thread.currentThread().getName() + " Clean Manager runs the cache clearing");
        return () -> caches.forEach(cache -> {
            if (cache != null) {
                cache.clear();
            }
        });
    }
}
