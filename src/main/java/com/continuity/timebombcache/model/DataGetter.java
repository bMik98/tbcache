package com.continuity.timebombcache.model;

import java.util.Collection;

@FunctionalInterface
public interface DataGetter<T> {

    Collection<T> getData();
}
