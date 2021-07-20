package com.continuity.timebombcache.service;

public interface CyclicService {

    void startCyclicClean();

    void shutdown();

    boolean isRunning();
}
