package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.AppEvent;
import com.continuity.timebombcache.model.HasIntegerId;
import com.continuity.timebombcache.model.entity.AppEventType;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.service.AppEventManager;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class AbstractTimeBombCache<T extends HasIntegerId> implements TimeBombCache<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractTimeBombCache.class.getSimpleName());

    private final RestApiClient<T> apiClient;
    private final long ttlInMillis;

    private final AtomicReference<Timer> timer = new AtomicReference<>();
    private final AtomicReference<Future<Collection<T>>> updater = new AtomicReference<>();
    private final AtomicReference<Collection<T>> cache = new AtomicReference<>();

    protected AbstractTimeBombCache(RestApiClient<T> apiClient, int ttlInSeconds, AppEventManager eventManager) {
        this.apiClient = apiClient;
        this.ttlInMillis = ttlInSeconds * 1000L;
        eventManager.addEventListener(this);
    }

    @Override
    public Collection<T> getData() {
        while (true) {
            Collection<T> existing = cache.get();
            if (existing != null) {
                return existing;
            }
            FutureTask<Collection<T>> ft = new FutureTask<>(() -> {
                Collection<T> data = apiClient.getData();
                cache.set(data);
                setTimer();
                return data;
            });
            if (updater.compareAndSet(null, ft)) {
                LOGGER.info(() -> Thread.currentThread().getName() + " run update");
                ft.run();
            }
            try {
                Collection<T> res = updater.get().get();
                if (res != null) {
                    LOGGER.info(() -> Thread.currentThread().getName() + " got result of size = " + res.size());
                    return res;
                }
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
        if (updater.get() == null || cache.get() == null) {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear skipped - cache is empty");
            return;
        } else {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear is waiting for running update");
            get();
        }
        LOGGER.info(() -> Thread.currentThread().getName() + " clear");
        updater.set(null);
        cache.set(null);
        cancelTimer();
    }

    @Override
    public void handleEvent(AppEvent event) {
        if (event.getType() == AppEventType.CLEAR_CACHE) {
            clear();
        }
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

    private void setTimer() {
        Timer newTimer = new Timer("self-clear");
        cancelTimer();
        timer.set(newTimer);
        newTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                clear();
            }
        }, ttlInMillis);
    }

    private void cancelTimer() {
        Timer currentTimer = timer.get();
        if (currentTimer != null) {
            currentTimer.cancel();
            currentTimer.purge();
        }
        timer.set(null);
    }
}
