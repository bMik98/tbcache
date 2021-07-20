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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class AbstractTimeBombCache<T extends HasIntegerId> implements TimeBombCache<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractTimeBombCache.class.getSimpleName());

    private final RestApiClient<T> apiClient;
    private final long ttlInMillis;

    private final AtomicReference<Timer> timer = new AtomicReference<>();
    private final AtomicReference<Future<Collection<T>>> updater = new AtomicReference<>();
    private final AtomicBoolean clearInProgress = new AtomicBoolean(false);

    protected AbstractTimeBombCache(RestApiClient<T> apiClient, int ttlInSeconds, AppEventManager eventManager) {
        this.apiClient = apiClient;
        this.ttlInMillis = ttlInSeconds * 1000L;
        eventManager.addEventListener(this);
    }

    @Override
    public Collection<T> getData() {
        while (true) {
            FutureTask<Collection<T>> ft = new FutureTask<>(() -> {
                Collection<T> data = apiClient.getData();
                setTimer();
                return data;
            });
            if (updater.compareAndSet(null, ft)) {
                LOGGER.info(() -> Thread.currentThread().getName() + " run update");
                ft.run();
            }
            Future<Collection<T>> future = updater.get();
            if (future != null) {
                try {
                    Collection<T> res = future.get();
                    if (res != null) {
                        LOGGER.info(() -> Thread.currentThread().getName() + " got result of size = " + res.size());
                        return res;
                    }
                } catch (ExecutionException e) {
                    LOGGER.severe(() -> Thread.currentThread().getName() + " " + e.getCause().getMessage());
                    updater.set(null);
                } catch (InterruptedException e) {
                    LOGGER.severe(() -> Thread.currentThread().getName() + " was interrupted when data receiving");
                    updater.set(null);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void clear() {
        Future<Collection<T>> future = updater.get();
        if (future == null) {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear skipped - cache is empty");
            return;
        }
        if (clearInProgress.compareAndSet(false, true)) {
            cancelTimer();
            try {
                LOGGER.info(() -> Thread.currentThread().getName() + " clear is waiting for running update");
                future.get();
            } catch (ExecutionException e) {
                LOGGER.severe(() -> Thread.currentThread().getName() + " " + e.getCause().getMessage());
            } catch (InterruptedException e) {
                LOGGER.severe(() -> Thread.currentThread().getName() + " clear was interrupted");
                Thread.currentThread().interrupt();
            } finally {
                updater.set(null);
                clearInProgress.set(false);
                LOGGER.info(() -> Thread.currentThread().getName() + " clear done");
            }
        } else {
            LOGGER.info(() -> Thread.currentThread().getName() + " clear already in progress");
        }
    }

    @Override
    public void handleEvent(AppEvent event) {
        if (event.getType() == AppEventType.CLEAR_CACHE) {
            clear();
        }
    }

    private void setTimer() {
        cancelTimer();
        Timer newTimer = new Timer("self-clear");
        newTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                clear();
            }
        }, ttlInMillis);
        timer.set(newTimer);
    }

    private void cancelTimer() {
        Timer currentTimer = timer.getAndSet(null);
        if (currentTimer != null) {
            currentTimer.cancel();
            currentTimer.purge();
        }
    }
}
