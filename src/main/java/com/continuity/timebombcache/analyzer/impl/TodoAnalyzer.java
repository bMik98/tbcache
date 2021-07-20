package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.model.DataGetter;
import com.continuity.timebombcache.model.entity.Todo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TodoAnalyzer {

    private static final Function<Map.Entry<Integer, Integer>, String> userTodosToString = entry ->
            "User " + entry.getKey() + " has " + entry.getValue() + " incomplete tasks";

    private static final Function<Todo, String> todoToString = todo ->
            "Todo #" + todo.getId() + " " + todo.getTitle();

    private final DataGetter<Todo> todoGetter;

    public TodoAnalyzer(DataGetter<Todo> todoGetter) {
        this.todoGetter = todoGetter;
    }

    public CompletableFuture<Collection<String>> uncompletedTasks() {
        return CompletableFuture.supplyAsync(todoGetter::getData)
                .thenApply(data -> data.stream()
                        .filter(todo -> !todo.isCompleted())
                        .collect(Collectors.toMap(Todo::getUserId, todo -> 1, Integer::sum)))
                .thenApply(map -> map.entrySet().stream()
                        .map(userTodosToString)
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<Collection<String>> uncompletedUserTasks(int userId) {
        return CompletableFuture.supplyAsync(todoGetter::getData)
                .thenApply(data -> data.stream()
                        .filter(todo -> todo.getUserId() == userId)
                        .filter(todo -> !todo.isCompleted())
                        .map(todoToString)
                        .collect(Collectors.toList()));
    }
}
