package ru.tdd.telegram_bot.app.commands.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Обработчик встроенных комманд
 */
public interface InlineBotCommandHandler extends CommandHandler {

    void handle(TelegramLongPollingBot bot, MaybeInaccessibleMessage message);
}
