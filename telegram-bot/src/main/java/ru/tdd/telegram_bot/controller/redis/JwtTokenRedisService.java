package ru.tdd.telegram_bot.controller.redis;

import ru.tdd.telegram_bot.app.exceptions.AppException;

import java.util.Optional;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Сервис для работы с хранилещем jwt токенов-редис
 */
public interface JwtTokenRedisService {

    /**
     * Добавление токена в хранилище-редис
     * @param chatId идентификатор пользователя в телеграме
     * @param token токен пользователя
     */
    void setToken(Long chatId, String token);

    /** Получение токена из хранилища-редис */
    Optional<String> getToken(Long chatId) throws AppException;
}
