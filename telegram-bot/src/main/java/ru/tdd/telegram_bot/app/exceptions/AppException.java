package ru.tdd.telegram_bot.app.exceptions;

/**
 * @author Tribushko Danil
 * @since 27.12.2025
 * Исключение приложения
 */
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }
}
