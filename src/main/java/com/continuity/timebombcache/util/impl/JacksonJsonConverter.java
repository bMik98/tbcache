package com.continuity.timebombcache.util.impl;

import com.continuity.timebombcache.util.ConversionException;
import com.continuity.timebombcache.util.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.Collection;

public class JacksonJsonConverter<T> implements JsonConverter<T> {

    private final ObjectMapper objectMapper;
    private final CollectionType javaType;

    public JacksonJsonConverter(Class<T> clazz) {
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.javaType = objectMapper.getTypeFactory()
                .constructCollectionType(Collection.class, clazz);
    }

    @Override
    public Collection<T> convert(String jsonArray) throws ConversionException {
        try {
            return objectMapper.readValue(jsonArray, javaType);
        } catch (JsonProcessingException e) {
            throw new ConversionException(e);
        }
    }
}
