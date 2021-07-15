package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.HasId;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface TimeBombCache<T extends HasId> {

    CompletableFuture<Collection<T>> getData();

    void clear();
}
