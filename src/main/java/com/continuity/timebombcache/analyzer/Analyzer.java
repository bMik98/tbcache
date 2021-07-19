package com.continuity.timebombcache.analyzer;

import java.util.Collection;

public interface Analyzer {

    Collection<String> uncompletedTasks();

    Collection<String> uncompletedUserTasks(int userId);

    Collection<String> userPostReplies();

    Collection<String> userAlbums(int userId, int albumPhotosThreshold);
}
