package ru.tdd.telegram_bot.app.commands.handlers;

import ru.tdd.telegram_bot.model.enums.BotCommand;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик команд бота
 */
public interface CommandHandler {

    BotCommand commandForHandle();
}
