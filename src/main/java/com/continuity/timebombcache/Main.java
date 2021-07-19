package com.continuity.timebombcache;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.analyzer.impl.SimpleAnalyzer;
import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.cache.impl.*;
import com.continuity.timebombcache.model.*;
import com.continuity.timebombcache.rest.impl.DelayingRestApiClient;
import com.continuity.timebombcache.util.Stopper;
import com.continuity.timebombcache.util.impl.JacksonJsonConverter;
import com.continuity.timebombcache.util.impl.RandomDelayStopper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    private static final int TTL_IN_SECONDS = 5;
    private static final int MIN_DELAY = 2;
    private static final int MAX_DELAY = 6;
    private static final Stopper apiDelayStopper = new RandomDelayStopper(MIN_DELAY, MAX_DELAY);

    private final DelayingRestApiClient<Album> albumClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/albums"),
            new JacksonJsonConverter<>(Album.class),
            apiDelayStopper);
    private final DelayingRestApiClient<Comment> commentClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/comments"),
            new JacksonJsonConverter<>(Comment.class),
            apiDelayStopper);
    private final DelayingRestApiClient<Photo> photoClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/photos"),
            new JacksonJsonConverter<>(Photo.class),
            apiDelayStopper);
    private final DelayingRestApiClient<Post> postClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/posts"),
            new JacksonJsonConverter<>(Post.class),
            apiDelayStopper);
    private final DelayingRestApiClient<Todo> todoClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/todos"),
            new JacksonJsonConverter<>(Todo.class),
            apiDelayStopper);
    private final DelayingRestApiClient<User> userClient = new DelayingRestApiClient<>(
            new URL("https://jsonplaceholder.typicode.com/users"),
            new JacksonJsonConverter<>(User.class),
            apiDelayStopper);

    private final TimeBombCache<Album> albumCache = new AlbumTimeBombCache(albumClient, TTL_IN_SECONDS);
    private final TimeBombCache<Comment> commentCache = new CommentTimeBombCache(commentClient, TTL_IN_SECONDS);
    private final TimeBombCache<Photo> photoCache = new PhotoTimeBombCache(photoClient, TTL_IN_SECONDS);
    private final TimeBombCache<Post> postCache = new PostTimeBombCache(postClient, TTL_IN_SECONDS);
    private final TimeBombCache<Todo> todoCache = new TodoTimeBombCache(todoClient, TTL_IN_SECONDS);
    private final TimeBombCache<User> userCache = new UserTimeBombCache(userClient, TTL_IN_SECONDS);

    private final Analyzer analyzer = new SimpleAnalyzer(
            albumCache, commentCache, photoCache, postCache, todoCache, userCache);

    private final AtomicInteger getCounter = new AtomicInteger(0);
    private final AtomicInteger todoCounter = new AtomicInteger(0);

    private final Callable<Void> getUserData = () -> {
        Collection<User> users = userCache.getData();
        LOGGER.info(() -> Thread.currentThread().getName() + " " + users.size());
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
//            new Main().testTodoAnalyze();
            new Main().testPostCommentsAnalyze();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testTodoAnalyze() {
        System.out.println("--------------------------------");
        analyzer.uncompletedTasks().forEach(System.out::println);
        System.out.println("--------------------------------");
        analyzer.uncompletedTasks().forEach(System.out::println);
        System.out.println("--------------------------------");
        analyzer.uncompletedTasks().forEach(System.out::println);
        System.out.println("--------------------------------");
        analyzer.uncompletedUserTasks(4).forEach(System.out::println);
        System.out.println("--------------------------------");
    }

    private void testPostCommentsAnalyze() {
        System.out.println("--------------------------------");
        analyzer.userPostReplies().forEach(System.out::println);
        System.out.println("--------------------------------");
        analyzer.userPostReplies().forEach(System.out::println);
        System.out.println("--------------------------------");
        analyzer.userPostReplies().forEach(System.out::println);
        System.out.println("--------------------------------");
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
