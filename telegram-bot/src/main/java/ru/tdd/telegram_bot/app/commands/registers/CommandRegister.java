package ru.tdd.telegram_bot.app.commands.registers;

import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Реестр команд бота
 */
public interface CommandRegister {

    /** Получить все обработчик из реестра*/
    List<CommandHandler> getAllHandlers();
}
