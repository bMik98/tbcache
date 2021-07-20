package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.cache.TimeBombCache;
import com.continuity.timebombcache.model.Album;
import com.continuity.timebombcache.model.Photo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AlbumAnalyzer {

    private final TimeBombCache<Album> albumCache;
    private final TimeBombCache<Photo> photoCache;

    public AlbumAnalyzer(TimeBombCache<Album> albumCache, TimeBombCache<Photo> photoCache) {
        this.albumCache = albumCache;
        this.photoCache = photoCache;
    }

    private static boolean numberOfPhotosIsAcceptable(Integer numberOfPhotos, int threshold) {
        return numberOfPhotos != null && numberOfPhotos >= threshold;
    }

    public CompletableFuture<Collection<String>> userAlbums(int userId, int photosThreshold) {
        CompletableFuture<Map<Integer, Integer>> numberOfPhotosPerAlbum = CompletableFuture
                .supplyAsync(photoCache::getData)
                .thenApply(data -> data.stream()
                        .collect(Collectors.toMap(Photo::getAlbumId, photo -> 1, Integer::sum)));
        return CompletableFuture.supplyAsync(albumCache::getData)
                .thenCombine(numberOfPhotosPerAlbum, (albums, photos) -> albums.stream()
                        .filter(a -> a.getUserId() == userId)
                        .filter(a -> numberOfPhotosIsAcceptable(photos.get(a.getId()), photosThreshold))
                        .map(a -> a.getTitle() + " (" + photos.get(a.getId()) + " photos)")
                        .collect(Collectors.toList()));
    }
}
