package com.continuity.timebombcache.cache.impl;

import com.continuity.timebombcache.cache.AbstractTimeBombCache;
import com.continuity.timebombcache.model.Comment;
import com.continuity.timebombcache.rest.RestApiClient;

public class CommentTimeBombCache extends AbstractTimeBombCache<Comment> {

    public CommentTimeBombCache(RestApiClient<Comment> apiClient, int ttlInSeconds) {
        super(apiClient, ttlInSeconds);
    }
}
