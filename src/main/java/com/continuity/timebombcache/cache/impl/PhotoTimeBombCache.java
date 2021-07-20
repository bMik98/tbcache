package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.Photo;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.service.AppEventManager;

public class PhotoTimeBombCache extends AbstractTimeBombCache<Photo> {

    public PhotoTimeBombCache(RestApiClient<Photo> apiClient, int ttlInSeconds, AppEventManager eventManager) {
        super(apiClient, ttlInSeconds, eventManager);
    }
}
