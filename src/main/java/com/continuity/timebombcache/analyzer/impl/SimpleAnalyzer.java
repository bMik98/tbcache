package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.analyzer.Analyzer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SimpleAnalyzer implements Analyzer {

    @Override
    public Collection<String> uncompletedUserTasks() {
        return null;
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
