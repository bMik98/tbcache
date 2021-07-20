package com.continuity.timebombcache;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.analyzer.impl.AnalyzerImpl;
import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.cache.impl.*;
import com.continuity.timebombcache.model.Clearable;
import com.continuity.timebombcache.model.entity.*;
import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.rest.impl.DelayingRestApiClient;
import com.continuity.timebombcache.service.CleanManager;
import com.continuity.timebombcache.service.CyclicService;
import com.continuity.timebombcache.service.EventManager;
import com.continuity.timebombcache.util.Stopper;
import com.continuity.timebombcache.util.impl.JacksonJsonConverter;
import com.continuity.timebombcache.util.impl.RandomDelayStopper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class MainTest {
    private static final Logger LOGGER = Logger.getLogger(MainTest.class.getSimpleName());

    private static final int CACHE_TTL_IN_SECONDS = 15;

    private static final int EVENT_MIN_DELAY = 10;
    private static final int EVENT_MAX_DELAY = 15;

    private static final int CLEAN_SERVICE_MIN_DELAY = 15;
    private static final int CLEAN_SERVICE_MAX_DELAY = 20;

    private static final int REST_RESPONSE_MIN_DELAY = 5;
    private static final int REST_RESPONSE_MAX_DELAY = 20;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
    }

    public final Analyzer analyzer;
    public final Analyzer uncachedAnalyzer;
    private final EventManager eventManager;
    private final CyclicService cleanService;

    private final AtomicInteger counter = new AtomicInteger(0);

    public MainTest() throws MalformedURLException {
        eventManager = new EventManager(EVENT_MIN_DELAY, EVENT_MAX_DELAY);

        Stopper restStopper = new RandomDelayStopper(REST_RESPONSE_MIN_DELAY, REST_RESPONSE_MAX_DELAY);

        URL albumUrl = new URL("https://jsonplaceholder.typicode.com/albums");
        URL commentUrl = new URL("https://jsonplaceholder.typicode.com/comments");
        URL photoUrl = new URL("https://jsonplaceholder.typicode.com/photos");
        URL postUrl = new URL("https://jsonplaceholder.typicode.com/posts");
        URL todoUrl = new URL("https://jsonplaceholder.typicode.com/todos");
        URL userUrl = new URL("https://jsonplaceholder.typicode.com/users");

        RestApiClient<Album> albumClient = new DelayingRestApiClient<>(
                albumUrl, new JacksonJsonConverter<>(Album.class), restStopper);
        RestApiClient<Comment> commentClient = new DelayingRestApiClient<>(
                commentUrl, new JacksonJsonConverter<>(Comment.class), restStopper);
        RestApiClient<Photo> photoClient = new DelayingRestApiClient<>(
                photoUrl, new JacksonJsonConverter<>(Photo.class), restStopper);
        RestApiClient<Post> postClient = new DelayingRestApiClient<>(
                postUrl, new JacksonJsonConverter<>(Post.class), restStopper);
        RestApiClient<Todo> todoClient = new DelayingRestApiClient<>(
                todoUrl, new JacksonJsonConverter<>(Todo.class), restStopper);
        RestApiClient<User> userClient = new DelayingRestApiClient<>(
                userUrl, new JacksonJsonConverter<>(User.class), restStopper);

        TimeBombCache<Album> albumCache = new AlbumTimeBombCache(albumClient, CACHE_TTL_IN_SECONDS, eventManager);
        TimeBombCache<Comment> commentCache = new CommentTimeBombCache(commentClient, CACHE_TTL_IN_SECONDS, eventManager);
        TimeBombCache<Photo> photoCache = new PhotoTimeBombCache(photoClient, CACHE_TTL_IN_SECONDS, eventManager);
        TimeBombCache<Post> postCache = new PostTimeBombCache(postClient, CACHE_TTL_IN_SECONDS, eventManager);
        TimeBombCache<Todo> todoCache = new TodoTimeBombCache(todoClient, CACHE_TTL_IN_SECONDS, eventManager);
        TimeBombCache<User> userCache = new UserTimeBombCache(userClient, CACHE_TTL_IN_SECONDS, eventManager);

        List<Clearable> caches = Arrays.asList(albumCache, commentCache, photoCache, postCache, todoCache, userCache);
        cleanService = new CleanManager(caches, CLEAN_SERVICE_MIN_DELAY, CLEAN_SERVICE_MAX_DELAY);

        analyzer = new AnalyzerImpl(albumCache, commentCache, photoCache, postCache, todoCache, userCache);
        uncachedAnalyzer = new AnalyzerImpl(albumClient, commentClient, photoClient, postClient, todoClient, userClient);
    }

    public static void main(String[] args) throws Exception {
        MainTest mainTest = new MainTest();
        int packageSize = mainTest.createTaskPackage(mainTest.analyzer).size();
        int cycles = 30;
        int cycles2 = 2;
        long res = mainTest.analyzerTest(mainTest.analyzer, cycles);
        MainTest mainTest2 = new MainTest();
        long res2 = mainTest2.analyzerTest(mainTest.uncachedAnalyzer, cycles2);
        System.out.println("-----------------------------------------");
        System.out.printf("Cached analyzer accomplished: %d tasks for %d seconds %n", (cycles * packageSize), res / 1000);
        System.out.printf("Uncached analyzer accomplished: %d tasks for %d seconds %n", (cycles2 * packageSize), res2 / 1000);
    }

    private long analyzerTest(Analyzer analyzer, int cycles) throws InterruptedException, ExecutionException {
        counter.set(0);
        int packageSize = createTaskPackage(analyzer).size();
        final int expectedTasks = cycles * packageSize;

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<Void>> futures = Collections.emptyList();

        cleanService.startCyclicTask();
        eventManager.startCyclicTask();

        long runtime = System.currentTimeMillis();
        for (int i = 0; i < cycles; i++) {
            futures = executorService.invokeAll(createTaskPackage(analyzer));
        }
        for (Future<Void> f : futures) {
            f.get();
        }
        runtime = System.currentTimeMillis() - runtime;

        cleanService.shutdown();
        eventManager.shutdown();

        shutdown(executorService);

        System.out.println("completed tasks : " + counter.get() + " expected: " + expectedTasks);
        return runtime;
    }

    public List<Callable<Void>> createTaskPackage(Analyzer analyzer) {
        return Arrays.asList(
                () -> {
                    analyzer.userAlbums(1, 10)
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                },
                () -> {
                    analyzer.userPostReplies()
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                },
                () -> {
                    analyzer.uncompletedTasks()
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                },
                () -> {
                    analyzer.uncompletedUserTasks(1)
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                },
                () -> {
                    analyzer.userAlbums(2, 10)
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                },
                () -> {
                    analyzer.userPostReplies()
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                },
                () -> {
                    analyzer.uncompletedTasks()
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                },
                () -> {
                    analyzer.uncompletedUserTasks(1)
                            .forEach(System.out::println);
                    counter.incrementAndGet();
                    return null;
                }
        );
    }

    private void shutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
