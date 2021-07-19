package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.Photo;
import com.continuity.timebombcache.rest.RestApiClient;

public class PhotoTimeBombCache extends AbstractTimeBombCache<Photo> {

    public PhotoTimeBombCache(RestApiClient<Photo> apiClient, int ttlInSeconds) {
        super(apiClient, ttlInSeconds);
    }
}
