package ru.tdd.telegram_bot.app.utils;

import org.slf4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
     * @param log лог для логирования исключения
     */
    public static void sendBotMessage(
            TelegramLongPollingBot bot,
            SendMessage message,
            Logger log
    ) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
