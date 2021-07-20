package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.Todo;
import com.continuity.timebombcache.rest.RestApiClient;

public class TodoTimeBombCache extends AbstractTimeBombCache<Todo> {

    public TodoTimeBombCache(RestApiClient<Todo> apiClient, int ttlInSeconds) {
        super(apiClient, ttlInSeconds);
    }
}
