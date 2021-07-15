package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.HasId;
import com.continuity.timebombcache.rest.RestApiClient;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractTimeBombCache<T extends HasId> implements TimeBombCache<T> {

    private final RestApiClient<T> apiClient;
    private final ConcurrentMap<Integer, T> cache = new ConcurrentHashMap<>();

    private final AtomicBoolean updateInProgress = new AtomicBoolean(false);

    private CompletableFuture<Collection<T>> updater;

    protected AbstractTimeBombCache(RestApiClient<T> apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public CompletableFuture<Collection<T>> getData() {
        if (!cache.isEmpty()) {
            return CompletableFuture.supplyAsync(cache::values);
        }
        if (updateInProgress.compareAndSet(false, true)) {
            updater = CompletableFuture.supplyAsync(this::update);
        }
        return updater;
    }

    @Override
    public void clear() {
        if (updateInProgress.get() || cache.isEmpty()) {
            return;
        }
        cache.clear();
    }

    private Collection<T> update() {
        Map<Integer, T> data = apiClient.getData().stream()
                .collect(Collectors.toMap(HasId::getId, Function.identity()));
        cache.putAll(data);
        updateInProgress.set(false);
        return cache.values();
    }
}
