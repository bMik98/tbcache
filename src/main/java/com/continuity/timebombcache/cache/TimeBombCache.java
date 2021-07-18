package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.HasIntegerId;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface TimeBombCache<T extends HasIntegerId> {

    Collection<T> getData();

    void clear() throws ExecutionException, InterruptedException;
}
