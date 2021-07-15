package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.rest.RestApiClient;

public class UserTimeBombCache extends AbstractTimeBombCache {

    public UserTimeBombCache(RestApiClient apiClient) {
        super(apiClient);
    }
}
