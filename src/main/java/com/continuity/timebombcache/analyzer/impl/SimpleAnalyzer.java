package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.cache.impl.TodoTimeBombCache;
import com.continuity.timebombcache.model.Todo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SimpleAnalyzer implements Analyzer {

    private final TodoTimeBombCache todoCache;

    public SimpleAnalyzer(TodoTimeBombCache todoCache) {
        this.todoCache = todoCache;
    }

    @Override
    public CompletableFuture<Collection<String>> uncompletedUserTasks() {
        return CompletableFuture.supplyAsync(todoCache::getData)
                .thenApply(data -> data.stream()
                        .filter(todo -> !todo.isCompleted())
                        .collect(Collectors.toMap(Todo::getUserId, todo -> 1, Integer::sum)))
                .thenApply(map -> map.entrySet().stream()
                        .map(entry -> "User " + entry.getKey() + " has " + entry.getValue() + " incomplete tasks")
                        .collect(Collectors.toList()));
    }

    @Override
    public Collection<String> uncompletedUserTasks(int userId) {
        return null;
    }

    @Override
    public Collection<String> userPostReplies() {
        return null;
    }

    @Override
    public Collection<String> userAlbums(int userId, int albumPhotosThreshold) {
        return null;
    }
}
