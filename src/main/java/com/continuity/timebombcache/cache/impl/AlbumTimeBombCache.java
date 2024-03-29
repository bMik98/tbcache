package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.Album;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.service.AppEventManager;

public class AlbumTimeBombCache extends AbstractTimeBombCache<Album> {

    public AlbumTimeBombCache(RestApiClient<Album> apiClient, int ttlInSeconds, AppEventManager eventManager) {
        super(apiClient, ttlInSeconds, eventManager);
    }
}
