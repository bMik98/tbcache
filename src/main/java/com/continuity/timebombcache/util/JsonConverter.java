package com.continuity.timebombcache.util;

import java.util.Collection;

public interface JsonConverter<T> {

    Collection<T> convert(String jsonArray) throws ConversionException;
}
