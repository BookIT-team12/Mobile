package com.example.bookit.app;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
    @Override
    public JsonElement serialize(LocalDateTime dateTime, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
