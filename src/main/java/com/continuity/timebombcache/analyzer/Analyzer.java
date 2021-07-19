package com.continuity.timebombcache.analyzer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Analyzer {

    CompletableFuture<Collection<String>> uncompletedTasks();

    CompletableFuture<Collection<String>>  uncompletedUserTasks(int userId);

    CompletableFuture<Collection<String>>  userPostReplies();

    CompletableFuture<Collection<String>>  userAlbums(int userId, int albumPhotosThreshold);
}
