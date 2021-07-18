package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.User;
import com.continuity.timebombcache.rest.RestApiClient;

public class UserTimeBombCache extends AbstractTimeBombCache<User> {

    public UserTimeBombCache(RestApiClient<User> apiClient) {
        super(apiClient);
    }
}
