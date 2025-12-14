package ru.tdd.telegram_bot.app.commands.registers;

import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;
import ru.tdd.telegram_bot.model.enums.Role;

import java.util.List;
import java.util.Map;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Реестр команд бота
 */
public interface CommandRegister {

    /** Получить все обработчик из реестра*/
    Map<Role, List<CommandHandler>> getAllHandlers();
}
