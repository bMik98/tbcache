package com.continuity.timebombcache.service;

import com.continuity.timebombcache.model.AppEvent;

public interface AppEventListener {

    void handleEvent(AppEvent event);
}
