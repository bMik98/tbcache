package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.model.DataGetter;
import com.continuity.timebombcache.model.entity.Comment;
import com.continuity.timebombcache.model.entity.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PostAnalyzer {

    private static final Function<Map.Entry<Integer, Integer>, String> asString = e ->
            "User #" + e.getKey() + " posted " + e.getValue() + " posts and for each post a few users commented";

    private final DataGetter<Comment> commentGetter;
    private final DataGetter<Post> postGetter;

    public PostAnalyzer(DataGetter<Comment> commentGetter, DataGetter<Post> postGetter) {
        this.commentGetter = commentGetter;
        this.postGetter = postGetter;
    }

    public CompletableFuture<Collection<String>> userPostReplies() {
        CompletableFuture<Map<Integer, Integer>> numberOfCommentsPerPost = CompletableFuture
                .supplyAsync(commentGetter::getData)
                .thenApply(data -> data.stream()
                        .collect(Collectors.toMap(Comment::getPostId, comment -> 1, Integer::sum)));
        return CompletableFuture.supplyAsync(postGetter::getData)
                .thenCombine(numberOfCommentsPerPost, (posts, comments) -> posts.stream()
                        .filter(p -> comments.get(p.getId()) != null)
                        .collect(Collectors.toMap(Post::getUserId, p -> 1, Integer::sum)))
                .thenApply(map -> map.entrySet().stream()
                        .map(asString)
                        .collect(Collectors.toList()));
    }
}
