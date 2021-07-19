package com.continuity.timebombcache;

import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.cache.impl.UserTimeBombCache;
import com.continuity.timebombcache.model.User;
import com.continuity.timebombcache.rest.impl.DelayingRestApiClient;
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
            new RandomDelayStopper(2, 6));

    private final TimeBombCache<User> userCache = new UserTimeBombCache(userClient, 5);

    private final Callable<Void> getUserData = () -> {
        Collection<User> users4 = userCache.getData();
        LOGGER.info(() -> Thread.currentThread().getName() + " " + users4.size());
//        users4.forEach(u -> System.out.println(u.getId() + " " + u.getEmail()));
        getCounter.incrementAndGet();
        return null;
    };

    private final Callable<Void> clearUserCache = () -> {
        TimeUnit.SECONDS.sleep(1);
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

    private void start() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(6);

        List<Callable<Void>> tasks = Arrays.asList(
                getUserData,
                clearUserCache,
                getUserData,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                clearUserCache,
                clearUserCache,
                getUserData,
                getUserData,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                clearUserCache,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                getUserData,
                clearUserCache,
                clearUserCache,
                getUserData,

                clearUserCache,
                clearUserCache,
                clearUserCache,
                clearUserCache,
                getUserData,
                getUserData,
                clearUserCache

        );

        executorService.invokeAll(tasks);

        executorService.shutdown();
        executorService.awaitTermination(320, TimeUnit.SECONDS);

        System.out.println("Gets : " + getCounter.get());


        ExecutorService e2 = Executors.newSingleThreadExecutor();
        e2.submit(getUserData);
        e2.submit(getUserData);
        e2.submit(getUserData);
        e2.submit(clearUserCache);
        e2.submit(getUserData);
        e2.submit(getUserData);
        e2.submit(getUserData);
        e2.submit(clearUserCache);

        e2.shutdown();
        e2.awaitTermination(120, TimeUnit.SECONDS);
    }

}
