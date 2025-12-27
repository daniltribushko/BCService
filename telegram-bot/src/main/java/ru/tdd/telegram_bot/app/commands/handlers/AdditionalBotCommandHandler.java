package ru.tdd.telegram_bot.app.commands.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик дополнительных комманд
 */
public interface AdditionalBotCommandHandler extends CommandHandler {

    void handle(TelegramLongPollingBot bot, Message message, BotCommandDTO commandDto);

}
