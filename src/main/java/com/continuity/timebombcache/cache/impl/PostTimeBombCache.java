package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.Post;
import com.continuity.timebombcache.rest.RestApiClient;

public class PostTimeBombCache extends AbstractTimeBombCache<Post> {

    public PostTimeBombCache(RestApiClient<Post> apiClient, int ttlInSeconds) {
        super(apiClient, ttlInSeconds);
    }
}
