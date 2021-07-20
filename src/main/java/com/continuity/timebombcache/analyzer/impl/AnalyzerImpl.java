package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.model.*;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class AnalyzerImpl implements Analyzer {

    private final TodoAnalyzer todoAnalyzer;
    private final PostAnalyzer postAnalyzer;
    private final AlbumAnalyzer albumAnalyzer;

    public AnalyzerImpl(
            TimeBombCache<Album> albumCache,
            TimeBombCache<Comment> commentCache,
            TimeBombCache<Photo> photoCache,
            TimeBombCache<Post> postCache,
            TimeBombCache<Todo> todoCache,
            TimeBombCache<User> userCache) {
        this.todoAnalyzer = new TodoAnalyzer(todoCache);
        this.postAnalyzer = new PostAnalyzer(commentCache, postCache);
        this.albumAnalyzer = new AlbumAnalyzer(albumCache, photoCache);
    }

    @Override
    public Collection<String> uncompletedTasks() {
        try {
            return todoAnalyzer.uncompletedTasks().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    @Override
    public Collection<String> uncompletedUserTasks(int userId) {
        try {
            return todoAnalyzer.uncompletedUserTasks(userId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    @Override
    public Collection<String> userPostReplies() {
        try {
            return postAnalyzer.userPostReplies().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    @Override
    public Collection<String> userAlbums(int userId, int photosThreshold) {
        try {
            return albumAnalyzer.userAlbums(userId, photosThreshold).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
}
