package com.continuity.timebombcache.analyzer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Analyzer {

    CompletableFuture<Collection<String>> uncompletedUserTasks();

    Collection<String> uncompletedUserTasks(int userId);

    Collection<String> userPostReplies();

    Collection<String> userAlbums(int userId, int albumPhotosThreshold);
}
