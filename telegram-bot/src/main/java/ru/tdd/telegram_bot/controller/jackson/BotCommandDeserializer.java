package ru.tdd.telegram_bot.controller.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ru.tdd.telegram_bot.app.utils.TextUtils;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.BotCommandsUtils;

import java.io.IOException;

/**
 * @author Tribusko Danil
 * @since 20.12.2025
 * Десериализатор для команды бота
 */
public class BotCommandDeserializer extends JsonDeserializer<BotCommand> {

    @Override
    public BotCommand deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();

        if (!TextUtils.isEmptyWithNull(value)) {
            return BotCommandsUtils.valueOfOpt(value)
                    .orElseThrow(() -> new IllegalArgumentException("Команда бота: " + value + " не найдена"));
        } else {
            return null;
        }
    }
}
