package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.Todo;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.service.AppEventManager;

public class TodoTimeBombCache extends AbstractTimeBombCache<Todo> {

    public TodoTimeBombCache(RestApiClient<Todo> apiClient, int ttlInSeconds, AppEventManager eventManager) {
        super(apiClient, ttlInSeconds, eventManager);
    }
}
