package com.continuity.timebombcache.analyzer.impl;

import com.continuity.timebombcache.model.DataGetter;
import com.continuity.timebombcache.model.entity.Album;
import com.continuity.timebombcache.model.entity.Photo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AlbumAnalyzer {

    private final DataGetter<Album> albumGetter;
    private final DataGetter<Photo> photoGetter;

    public AlbumAnalyzer(DataGetter<Album> albumGetter, DataGetter<Photo> photoGetter) {
        this.albumGetter = albumGetter;
        this.photoGetter = photoGetter;
    }

    private static boolean numberOfPhotosIsAcceptable(Integer numberOfPhotos, int threshold) {
        return numberOfPhotos != null && numberOfPhotos >= threshold;
    }

    public CompletableFuture<Collection<String>> userAlbums(int userId, int photosThreshold) {
        CompletableFuture<Map<Integer, Integer>> numberOfPhotosPerAlbum = CompletableFuture
                .supplyAsync(photoGetter::getData)
                .thenApply(data -> data.stream()
                        .collect(Collectors.toMap(Photo::getAlbumId, photo -> 1, Integer::sum)));
        return CompletableFuture.supplyAsync(albumGetter::getData)
                .thenCombine(numberOfPhotosPerAlbum, (albums, photos) -> albums.stream()
                        .filter(a -> a.getUserId() == userId)
                        .filter(a -> numberOfPhotosIsAcceptable(photos.get(a.getId()), photosThreshold))
                        .map(a -> a.getTitle() + " (" + photos.get(a.getId()) + " photos)")
                        .collect(Collectors.toList()));
    }
}
