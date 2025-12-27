package ru.tdd.telegram_bot.controller.jackson.date_ime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.tdd.telegram_bot.app.utils.TextUtils;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Десериализатор LocalDate
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();

        if (!TextUtils.isEmptyWithNull(value))
            return LocalDate.parse(value);
        else
            return null;
    }
}
