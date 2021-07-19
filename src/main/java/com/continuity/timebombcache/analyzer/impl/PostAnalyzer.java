package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.model.Comment;
import com.continuity.timebombcache.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PostAnalyzer {

    private static final Function<Map.Entry<Integer, Integer>, String> asString = e ->
            "User #" + e.getKey() + " posted " + e.getValue() + " posts and for each post a few users commented";

    private final TimeBombCache<Comment> commentCache;
    private final TimeBombCache<Post> postCache;

    public PostAnalyzer(TimeBombCache<Comment> commentCache, TimeBombCache<Post> postCache) {
        this.commentCache = commentCache;
        this.postCache = postCache;
    }

    public CompletableFuture<Collection<String>> userPostReplies() {
        CompletableFuture<Map<Integer, Integer>> numberOfCommentsPerPost = CompletableFuture
                .supplyAsync(commentCache::getData)
                .thenApply(data -> data.stream()
                        .collect(Collectors.toMap(Comment::getPostId, comment -> 1, Integer::sum)));
        return CompletableFuture.supplyAsync(postCache::getData)
                .thenCombine(numberOfCommentsPerPost, (posts, comments) -> posts.stream()
                        .filter(p -> comments.get(p.getId()) != null)
                        .collect(Collectors.toMap(Post::getUserId, p -> 1, Integer::sum)))
                .thenApply(map -> map.entrySet().stream()
                        .map(asString)
                        .collect(Collectors.toList()));
    }
}
