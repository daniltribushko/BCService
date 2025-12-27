package ru.tdd.telegram_bot.controller.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.tdd.telegram_bot.model.enums.BotCommand;

import java.io.IOException;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Сериализатор команд бота
 */
public class BotCommandSerializer extends JsonSerializer<BotCommand> {
    @Override
    public void serialize(BotCommand botCommand, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (botCommand != null) {
            jsonGenerator.writeString(botCommand.getText());
        } else {
            jsonGenerator.writeNull();
        }
    }
}
