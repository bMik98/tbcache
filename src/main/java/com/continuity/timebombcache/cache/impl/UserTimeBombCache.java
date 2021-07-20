package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.User;
import com.continuity.timebombcache.rest.RestApiClient;

public class UserTimeBombCache extends AbstractTimeBombCache<User> {

    public UserTimeBombCache(RestApiClient<User> apiClient, int ttlInSeconds) {
        super(apiClient, ttlInSeconds);
    }
}
