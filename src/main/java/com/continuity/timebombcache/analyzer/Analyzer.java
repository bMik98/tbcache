package com.continuity.timebombcache.analyzer;

import java.util.Collection;

public interface Analyzer {

    Collection<String> uncompletedUserTasks();

    Collection<String> uncompletedUserTasks(int userId);

    Collection<String> userPostReplies();

    Collection<String> userAlbums(int userId, int albumPhotosThreshold);
}
