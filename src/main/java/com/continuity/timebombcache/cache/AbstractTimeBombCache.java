package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.HasIntegerId;
import com.continuity.timebombcache.rest.RestApiClient;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class AbstractTimeBombCache<T extends HasIntegerId> implements TimeBombCache<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractTimeBombCache.class.getSimpleName());

    private final RestApiClient<T> apiClient;
    private final ScheduledExecutorService selfCleanService = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<Future<Collection<T>>> updater = new AtomicReference<>();

    protected AbstractTimeBombCache(RestApiClient<T> apiClient, int ttlInSeconds) {
        this.apiClient = apiClient;
        selfCleanService.scheduleAtFixedRate(this::clear, 0, ttlInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Collection<T> getData() {
        while (true) {
            FutureTask<Collection<T>> ft = new FutureTask<>(apiClient::getData);
            if (updater.compareAndSet(null, ft)) {
                LOGGER.info(() -> Thread.currentThread().getName() + " run update");
                ft.run();
            }
            try {
                Collection<T> res = updater.get().get();
                LOGGER.info(() -> Thread.currentThread().getName() + " got result of size = " + res.size());
                return res;
            } catch (ExecutionException e) {
                LOGGER.severe(() -> Thread.currentThread().getName() + " " + e.getCause().getMessage());
                updater.set(null);
            } catch (InterruptedException e) {
                LOGGER.severe(() -> Thread.currentThread().getName() + " was interrupted when receiving data");
                updater.set(null);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void clear() {
        if (updater.get() == null) {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear skipped - cache is empty");
            return;
        } else {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear is waiting for running update");
            get();
        }
        LOGGER.info(() -> Thread.currentThread().getName() + " clear");
        updater.set(null);
    }

    private Collection<T> get() {
        try {
            Collection<T> res = updater.get().get();
            LOGGER.info(() -> Thread.currentThread().getName() + " got result of size = " + res.size());
            return res;
        } catch (ExecutionException e) {
            LOGGER.severe(() -> Thread.currentThread().getName() + " " + e.getCause().getMessage());
            updater.set(null);
        } catch (InterruptedException e) {
            LOGGER.severe(() -> Thread.currentThread().getName() + " was interrupted when receiving data");
            updater.set(null);
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
