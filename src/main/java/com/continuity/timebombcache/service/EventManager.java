package com.continuity.timebombcache.service;

import com.continuity.timebombcache.model.AppEvent;
import com.continuity.timebombcache.model.entity.AppEventType;

import java.util.ArrayList;
import java.util.List;

public class EventManager extends AbstractRandomlyCyclicService implements AppEventManager {

    private final List<AppEventListener> listeners = new ArrayList<>();

    public EventManager(int minDelayInSeconds, int maxDelayInSeconds) {
        super(minDelayInSeconds, maxDelayInSeconds);
    }

    @Override
    public void addEventListener(AppEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    protected Runnable task() {
        AppEvent event = () -> AppEventType.CLEAR_CACHE;
        listeners.forEach(listener -> listener.handleEvent(event));
        return null;
    }
}
