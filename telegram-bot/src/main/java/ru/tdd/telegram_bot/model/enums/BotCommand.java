package ru.tdd.telegram_bot.model.enums;

import java.io.Serializable;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Комманда бота
 */
public interface BotCommand extends Serializable {

    String getText();

    Role getRole();
}
