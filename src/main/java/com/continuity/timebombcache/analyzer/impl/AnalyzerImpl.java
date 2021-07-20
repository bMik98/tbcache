package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.analyzer.Analyzer;
import com.continuity.timebombcache.model.DataGetter;
import com.continuity.timebombcache.model.entity.*;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class AnalyzerImpl implements Analyzer {

    private final TodoAnalyzer todoAnalyzer;
    private final PostAnalyzer postAnalyzer;
    private final AlbumAnalyzer albumAnalyzer;

    public AnalyzerImpl(
            DataGetter<Album> albumGetter,
            DataGetter<Comment> commentGetter,
            DataGetter<Photo> photoGetter,
            DataGetter<Post> postGetter,
            DataGetter<Todo> todoGetter,
            DataGetter<User> userGetter) {
        this.todoAnalyzer = new TodoAnalyzer(todoGetter);
        this.postAnalyzer = new PostAnalyzer(commentGetter, postGetter);
        this.albumAnalyzer = new AlbumAnalyzer(albumGetter, photoGetter);
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
