package ru.tdd.telegram_bot.app.commands.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tdd.telegram_bot.model.enums.BotCommand;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик основных комманд бота
 */
public interface MainCommandHandler extends CommandHandler {

    void handle(TelegramLongPollingBot bot, Message message);

}
