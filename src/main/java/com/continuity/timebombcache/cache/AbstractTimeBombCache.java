package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.HasIntegerId;
import com.continuity.timebombcache.rest.RestApiClient;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class AbstractTimeBombCache<T extends HasIntegerId> implements TimeBombCache<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractTimeBombCache.class.getSimpleName());

    private final RestApiClient<T> apiClient;
    private final AtomicReference<CompletableFuture<Collection<T>>> cache = new AtomicReference<>();
    private final AtomicBoolean updateInProgress = new AtomicBoolean(false);
//    private Map<Integer, T> cache = new HashMap<>();

//    private CompletableFuture<Collection<T>> updater;

    protected AbstractTimeBombCache(RestApiClient<T> apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public CompletableFuture<Collection<T>> getData() {
        if (cache.compareAndSet(null, update())) {
            LOGGER.info(() -> Thread.currentThread().getName() + " set update in progress ");
        } else {
            LOGGER.info(() -> Thread.currentThread().getName() + " update already in progress ");
        }
        return cache.get();
    }

    @Override
    public void clear() {
        if (cache.get() == null) {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear skipped - cache is empty");
            return;
        }
//        if (updateInProgress.get()) {
//            LOGGER.info(() -> Thread.currentThread().getName() + " clear skipped - update is in progress");
//            return;
//        }
//        cache.
        cache.set(null);
    }

    private CompletableFuture<Collection<T>> update() {
        return CompletableFuture.supplyAsync(() -> {
//            updateInProgress.set(true);
            LOGGER.info(Thread.currentThread().getName() + " starts update");
            Collection<T> result = apiClient.getData();
//            updateInProgress.set(false);
            return result;
        });
    }
}
