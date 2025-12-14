package ru.tdd.telegram_bot.app.utils;

import ru.tdd.telegram_bot.model.constants.RedisKeyNames;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Утилиты для работы с ключами хранилищь редис
 */
public class RedisKeysUtils {

    private RedisKeysUtils() {}

    public static String getCommandWithChatId(Long chatId, String key) {
        return chatId + ":" + key;
    }

    public static String getBotLastCommandKey(Long chatId) {
        return getCommandWithChatId(chatId, RedisKeyNames.BOT_LAST_COMMAND);
    }
}
