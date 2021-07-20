package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.Post;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.service.AppEventManager;

public class PostTimeBombCache extends AbstractTimeBombCache<Post> {

    public PostTimeBombCache(RestApiClient<Post> apiClient, int ttlInSeconds, AppEventManager eventManager) {
        super(apiClient, ttlInSeconds, eventManager);
    }
}
