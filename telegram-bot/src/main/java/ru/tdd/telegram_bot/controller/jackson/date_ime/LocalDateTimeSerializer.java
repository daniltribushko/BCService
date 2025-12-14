package ru.tdd.telegram_bot.controller.jackson.date_ime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Сериализатор LocalDateTime
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null)
            gen.writeString(value.toString());
        else
            gen.writeNull();
    }
}
