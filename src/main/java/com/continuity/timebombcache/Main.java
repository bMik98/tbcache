package com.continuity.timebombcache;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.analyzer.impl.AnalyzerImpl;
import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.cache.impl.*;
import com.continuity.timebombcache.model.Clearable;
import com.continuity.timebombcache.model.entity.*;
import com.continuity.timebombcache.rest.impl.DelayingRestApiClient;
import com.continuity.timebombcache.service.CleanManager;
import com.continuity.timebombcache.service.CyclicService;
import com.continuity.timebombcache.service.EventManager;
import com.continuity.timebombcache.util.Stopper;
import com.continuity.timebombcache.util.impl.FixedDelayStopper;
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

    private static final int TTL_IN_SECS = 5;
    private static final int MIN_DELAY = 2;
    private static final int MAX_DELAY = 6;
    private static final Stopper apiDelayStopper = new RandomDelayStopper(MIN_DELAY, MAX_DELAY);

    private final EventManager eventManager = new EventManager(1, 5);

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

    private final TimeBombCache<Album> albumCache = new AlbumTimeBombCache(albumClient, TTL_IN_SECS, eventManager);
    private final TimeBombCache<Comment> commentCache = new CommentTimeBombCache(commentClient, TTL_IN_SECS, eventManager);
    private final TimeBombCache<Photo> photoCache = new PhotoTimeBombCache(photoClient, TTL_IN_SECS, eventManager);
    private final TimeBombCache<Post> postCache = new PostTimeBombCache(postClient, TTL_IN_SECS, eventManager);
    private final TimeBombCache<Todo> todoCache = new TodoTimeBombCache(todoClient, TTL_IN_SECS, eventManager);
    private final TimeBombCache<User> userCache = new UserTimeBombCache(userClient, TTL_IN_SECS, eventManager);

    private final Collection<Clearable> caches = Arrays.asList(
            albumCache, commentCache, photoCache, postCache, todoCache, userCache);

    private final CyclicService cleanService = new CleanManager(caches, 1, 5);

    private final Analyzer analyzer = new AnalyzerImpl(
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
            new Main().start();
//            new Main().testTodoAnalyze();
//            new Main().testPostCommentsAnalyze();
//            new Main().testAlbumAnalyze();
//            new Main().testCleanService();
//            new Main().testEventManager();
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

    private void testAlbumAnalyze() {
        System.out.println("--------------------------------");
        analyzer.userAlbums(1, 0).forEach(System.out::println);
        System.out.println("--------------------------------");
        analyzer.userAlbums(2, 60).forEach(System.out::println);
        System.out.println("--------------------------------");
        analyzer.userAlbums(3, 0).forEach(System.out::println);
        System.out.println("--------------------------------");
    }

    private void testCleanService() {
        System.out.println(cleanService.isRunning());
        cleanService.startCyclicClean();
        System.out.println(cleanService.isRunning());
        new FixedDelayStopper(10).delay();
        cleanService.shutdown();
        System.out.println(cleanService.isRunning());
        cleanService.startCyclicClean();
        new FixedDelayStopper(10).delay();
        cleanService.shutdown();

    }

    private void testEventManager() {
        System.out.println(eventManager.isRunning());
        eventManager.startCyclicClean();
        System.out.println(eventManager.isRunning());
        new FixedDelayStopper(10).delay();
        eventManager.shutdown();
        System.out.println(eventManager.isRunning());
        eventManager.startCyclicClean();
        new FixedDelayStopper(10).delay();
        eventManager.shutdown();
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
