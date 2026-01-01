package ru.tdd.telegram_bot.app.exceptions;

/**
 * @author Tribusko Danil
 * @since 01.01.2026
 * Обычная ошибка
 */
public class SimpleRuntimeException extends RuntimeException {

    public SimpleRuntimeException(String message) {
        super(message);
    }
}
