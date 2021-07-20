package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.entity.Comment;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.service.AppEventManager;

public class CommentTimeBombCache extends AbstractTimeBombCache<Comment> {

    public CommentTimeBombCache(RestApiClient<Comment> apiClient, int ttlInSeconds, AppEventManager eventManager) {
        super(apiClient, ttlInSeconds, eventManager);
    }
}
