package ru.tdd.telegram_bot.model.constants;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Набор ключей хранилищь redis
 */
public class RedisKeyNames {

    public static final String BOT_LAST_COMMAND = "bot-last-command";

    public static final String JWT_TOKEN = "jwt-token";

    public static final String CURRENT_USER = "current-user";

    private RedisKeyNames() {}
}
