package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.model.Todo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleAnalyzer implements Analyzer {

    private static final Function<Map.Entry<Integer, Integer>, String> userTodosToString = entry ->
            "User " + entry.getKey() + " has " + entry.getValue() + " incomplete tasks";

    private static final Function<Todo, String> todoToString = todo ->
            "Todo #" + todo.getId() + " " + todo.getTitle();

    private final TimeBombCache<Todo> todoCache;

    public SimpleAnalyzer(TimeBombCache<Todo> todoCache) {
        this.todoCache = todoCache;
    }

    @Override
    public CompletableFuture<Collection<String>> uncompletedTasks() {
        return CompletableFuture.supplyAsync(todoCache::getData)
                .thenApply(data -> data.stream()
                        .filter(todo -> !todo.isCompleted())
                        .collect(Collectors.toMap(Todo::getUserId, todo -> 1, Integer::sum)))
                .thenApply(map -> map.entrySet().stream()
                        .map(userTodosToString)
                        .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Collection<String>> uncompletedUserTasks(int userId) {
        return CompletableFuture.supplyAsync(todoCache::getData)
                .thenApply(data -> data.stream()
                        .filter(todo -> todo.getUserId() == userId)
                        .filter(todo -> !todo.isCompleted())
                        .map(todoToString)
                        .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Collection<String>> userPostReplies() {
        return null;
    }

    @Override
    public CompletableFuture<Collection<String>> userAlbums(int userId, int albumPhotosThreshold) {
        return null;
    }
}
