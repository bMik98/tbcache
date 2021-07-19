package com.continuity.timebombcache;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.analyzer.impl.SimpleAnalyzer;
import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.cache.impl.TodoTimeBombCache;
import com.continuity.timebombcache.cache.impl.UserTimeBombCache;
import com.continuity.timebombcache.model.Todo;
import com.continuity.timebombcache.model.User;
import com.continuity.timebombcache.rest.impl.DelayingRestApiClient;
import com.continuity.timebombcache.util.Stopper;
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


    private static final int TTL_IN_SECONDS = 5;
    private static final int MIN_DELAY = 2;
    private static final int MAX_DELAY = 6;
    private static final Stopper apiDelayStopper = new RandomDelayStopper(MIN_DELAY, MAX_DELAY);

    private final AtomicInteger getCounter = new AtomicInteger(0);
    private final AtomicInteger todoCounter = new AtomicInteger(0);

    private final DelayingRestApiClient<User> userClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/users"),
            new JacksonJsonConverter<>(User.class),
            apiDelayStopper);

    private final DelayingRestApiClient<Todo> todoClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/todos"),
            new JacksonJsonConverter<>(Todo.class),
            apiDelayStopper);

    private final TimeBombCache<User> userCache = new UserTimeBombCache(userClient, TTL_IN_SECONDS);
    private final TimeBombCache<Todo> todoCache = new TodoTimeBombCache(todoClient, TTL_IN_SECONDS);

    private final Analyzer analyzer = new SimpleAnalyzer(todoCache);

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
//            new Main().start();
            new Main().todo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void todo() {
        try {
            System.out.println("--------------------------------");
            Collection<String> res = analyzer.uncompletedTasks().get();
            res.forEach(System.out::println);
            System.out.println("--------------------------------");
            Collection<String> res2 = analyzer.uncompletedTasks().get();
            res2.forEach(System.out::println);
            System.out.println("--------------------------------");
            Collection<String> res3 = analyzer.uncompletedTasks().get();
            res3.forEach(System.out::println);
            System.out.println("--------------------------------");

            Collection<String> res4 = analyzer.uncompletedUserTasks(4).get();
            res4.forEach(System.out::println);
            System.out.println("--------------------------------");
        } catch (InterruptedException | ExecutionException e) {
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
