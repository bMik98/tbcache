package com.continuity.timebombcache.rest.impl;

import com.continuity.timebombcache.rest.RestApiClient;
import com.continuity.timebombcache.rest.RestCallException;
import com.continuity.timebombcache.util.ConversionException;
import com.continuity.timebombcache.util.JsonConverter;
import com.continuity.timebombcache.util.Stopper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DelayingRestApiClient<T> implements RestApiClient<T> {
    private static final Logger LOGGER = Logger.getLogger(DelayingRestApiClient.class.getSimpleName());

    private final URL url;
    private final JsonConverter<T> converter;
    private final Stopper stopper;

    public DelayingRestApiClient(URL url, JsonConverter<T> converter, Stopper stopper) {
        this.url = url;
        this.stopper = stopper;
        this.converter = converter;
    }

    private static HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    private Collection<T> parseBody(HttpURLConnection connection) throws ConversionException, IOException {
        String body = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        return converter.convert(body);
    }

    @Override
    public Collection<T> getData() {
        LOGGER.info(() -> Thread.currentThread().getName() + " gets data from " + url);
        stopper.delay();
        try {
            HttpURLConnection conn = openConnection(url);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RestCallException(conn.getResponseCode(), url);
            }
            Collection<T> result = parseBody(conn);
            conn.disconnect();
            LOGGER.info(() -> Thread.currentThread().getName() + " gets data DONE from " + url);
            return result;
        } catch (IOException | ConversionException e) {
            throw new RestCallException(e);
        }
    }
}
