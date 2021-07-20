package com.continuity.timebombcache.service;

import com.continuity.timebombcache.model.Clearable;

import java.util.Collection;

public class CleanManager extends AbstractRandomlyCyclicService {

    private final Collection<Clearable> caches;

    public CleanManager(Collection<Clearable> caches, int minDelayInSeconds, int maxDelayInSeconds) {
        super(minDelayInSeconds, maxDelayInSeconds);
        this.caches = caches;
    }

    @Override
    protected Runnable task() {
        return () -> caches.forEach(cache -> {
            if (cache != null) {
                cache.clear();
            }
        });
    }
}
