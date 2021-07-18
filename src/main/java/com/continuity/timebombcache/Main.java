package com.continuity.timebombcache;

import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.cache.UserTimeBombCache;
import com.continuity.timebombcache.model.User;
import com.continuity.timebombcache.rest.DelayingRestApiClient;
import com.continuity.timebombcache.util.impl.JacksonJsonConverter;
import com.continuity.timebombcache.util.impl.RandomDelayStopper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    private final AtomicInteger getCounter = new AtomicInteger(0);

    private final DelayingRestApiClient<User> userClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/users"),
            new JacksonJsonConverter<>(User.class),
            new RandomDelayStopper(5, 20));

    private final TimeBombCache<User> userCache = new UserTimeBombCache(userClient);

    private final Callable<Void> getUserData = () -> {
        Collection<User> users4 = userCache.getData().get();
        LOGGER.info(() -> Thread.currentThread().getName() + " " + users4.size());
        users4.forEach(u -> System.out.println(u.getId() + " " + u.getEmail()));
        getCounter.incrementAndGet();
        return null;
    };

    private final Callable<Void> clearUserCache = () -> {
        userCache.clear();
        return null;
    };

    public Main() throws MalformedURLException {
    }

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        List<Callable<Void>> tasks = Arrays.asList(
                getUserData,
                getUserData,
                getUserData,
                getUserData,

                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData
        );

        executorService.invokeAll(tasks);

        executorService.shutdown();
        executorService.awaitTermination(120, TimeUnit.SECONDS);

        System.out.println("Gets : " + getCounter.get());
    }

}
