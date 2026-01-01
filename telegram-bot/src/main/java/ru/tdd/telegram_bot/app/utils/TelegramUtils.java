package ru.tdd.telegram_bot.app.utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Утилиты для работы с телеграмом
 */
public class TelegramUtils {

    private TelegramUtils() {}

    /**
     * Отправка сообщения через бота с обработкой исключения телеграма
     * @param bot телеграм бот
     * @param message сообщение бота
     */
    public static void sendBotMessage(
            TelegramLongPollingBot bot,
            SendMessage message
    ) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new SimpleRuntimeException(e.getMessage());
        }
    }

    /**
     * Отправка сообщения через бота с обработкой исключения телеграма
     * @param bot телеграм бот
     * @param message сообщение бота
     */
    public static void sendBotMessage(
            TelegramLongPollingBot bot,
            EditMessageText message
    ) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new SimpleRuntimeException(e.getMessage());
        }
    }
}
