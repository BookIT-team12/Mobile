package com.example.bookit.utils.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatterWithMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private final DateTimeFormatter formatterWithoutMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final DateTimeFormatter formatterWithOptionalMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]");

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatterWithMillis.format(value));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            String dateString = in.nextString();

            // Try parsing with milliseconds format
            try {
                return LocalDateTime.parse(dateString, formatterWithMillis);
            } catch (Exception e) {
                // If parsing with milliseconds format fails, try without milliseconds
                try {
                    return LocalDateTime.parse(dateString, formatterWithoutMillis);
                } catch (Exception ignored) {
                    // If both formats fail, try the optional milliseconds format
                    return LocalDateTime.parse(dateString, formatterWithOptionalMillis);
                }
            }
        }
    }
}
