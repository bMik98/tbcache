package com.continuity.timebombcache;

import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.model.User;
import com.continuity.timebombcache.rest.DelayingRestApiClient;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.rest.RestCallException;
import com.continuity.timebombcache.util.impl.JacksonJsonConverter;
import com.continuity.timebombcache.util.impl.RandomDelayStopper;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class Main {

    public static void main(String[] args) throws IOException, RestCallException {
        // write your code here
        DelayingRestApiClient<User> client = new DelayingRestApiClient<>(
                new URL("https://jsonplaceholder.typicode.com/users"),
                new JacksonJsonConverter<>(User.class),
                new RandomDelayStopper(5, 20));

        TimeBombCache<User> userCache = new DelayingRestApiClient<>()

        Collection<User> users = client.getData();
        System.out.println(users.size());
        users.forEach(u -> System.out.println(u.getId() + " " + u.getEmail()));
    }
}
