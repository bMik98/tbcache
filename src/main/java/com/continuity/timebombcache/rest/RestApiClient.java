package com.continuity.timebombcache.rest;

import java.util.Collection;

public interface RestApiClient<T> {

    Collection<T> getData();
}
