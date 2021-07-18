package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.HasIntegerId;
import com.continuity.timebombcache.rest.RestApiClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractTimeBombCache<T extends HasIntegerId> implements TimeBombCache<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractTimeBombCache.class.getSimpleName());

    private final RestApiClient<T> apiClient;
    private final AtomicReference<CompletableFuture<Collection<T>>> updater;
    private final AtomicBoolean updateInProgress = new AtomicBoolean(false);
    private Map<Integer, T> cache = new HashMap<>();

//    private CompletableFuture<Collection<T>> updater;

    protected AbstractTimeBombCache(RestApiClient<T> apiClient) {
        this.apiClient = apiClient;
        LOGGER.info(() -> Thread.currentThread().getName() + " possibly get");
        this.updater = new AtomicReference<>(update());
        LOGGER.info(() -> Thread.currentThread().getName() + " possibly stop get");
    }

    @Override
    public CompletableFuture<Collection<T>> getData() {
        if (!cache.isEmpty()) {
            LOGGER.info(() -> Thread.currentThread().getName() + " get cache");
            return CompletableFuture.supplyAsync(cache::values);
        }
        if (updateInProgress.compareAndSet(false, true)) {
            LOGGER.info(() -> Thread.currentThread().getName() + " set update in progress ");
            updater.set(update());
        } else {
            LOGGER.info(() -> Thread.currentThread().getName() + " update already in progress ");
        }
        return updater.get();
    }

    @Override
    public void clear() {
        if (cache.isEmpty()) {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear skipped - cache is empty");
            return;
        }
        if (updateInProgress.get()) {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear skipped - update is in progress");
            return;
        }
        cache = new HashMap<>();
    }

    private CompletableFuture<Collection<T>> update() {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info(Thread.currentThread().getName() + " starts update");
            Collection<T> data = apiClient.getData();
            LOGGER.info(Thread.currentThread().getName() + " update cache");
            this.cache = data.stream()
                    .collect(Collectors.toMap(HasIntegerId::getId, Function.identity()));
            updateInProgress.set(false);
            return cache.values();
        });
    }
}
