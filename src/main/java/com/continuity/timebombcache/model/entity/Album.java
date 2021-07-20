package com.continuity.timebombcache.model.entity;

import com.continuity.timebombcache.model.HasIntegerId;

public class Album implements HasIntegerId {

    private int id;
    private int userId;
    private String title;

    public Album() {
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
