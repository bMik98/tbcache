package com.continuity.timebombcache.rest;

import java.net.URL;

public class RestCallException extends RuntimeException {

    public RestCallException(int responseCode, URL url) {
        super("Filed to connect to " + url + " Response Code: " + responseCode);
    }

    public RestCallException(Exception ex) {
        super(ex);
    }
}
