package ru.tdd.telegram_bot.model.enums;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Встоенные дополнительные команды
 */
public interface InlineBotCommand extends BotCommand {

    /** Название комманды для вывода пользователя */
    String getName();
}
