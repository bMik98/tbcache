package com.continuity.timebombcache.cache;

import com.continuity.timebombcache.model.Clearable;
import com.continuity.timebombcache.model.DataGetter;
import com.continuity.timebombcache.model.HasIntegerId;

public interface TimeBombCache<T extends HasIntegerId> extends DataGetter<T>, Clearable {
}
