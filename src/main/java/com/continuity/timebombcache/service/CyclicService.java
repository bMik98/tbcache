package com.continuity.timebombcache.service;

public interface CyclicService {

    void startCyclicTask();

    void shutdown();

    boolean isRunning();
}
