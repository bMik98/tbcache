package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.HasIntegerId;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface TimeBombCache<T extends HasIntegerId> {

    CompletableFuture<Collection<T>> getData();

    void clear();
}
