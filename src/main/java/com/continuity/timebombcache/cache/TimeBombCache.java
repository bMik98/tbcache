package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.Clearable;
import com.continuity.timebombcache.model.DataGetter;
import com.continuity.timebombcache.service.AppEventListener;

public interface TimeBombCache<T> extends DataGetter<T>, Clearable, AppEventListener {
}
