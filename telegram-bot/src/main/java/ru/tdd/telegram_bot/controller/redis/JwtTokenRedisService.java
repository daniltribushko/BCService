package ru.tdd.telegram_bot.controller.redis;

import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.model.dto.users.JwtTokenDto;

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
    void setToken(Long chatId, JwtTokenDto token);

    /** Получение токена из хранилища-редис */
    JwtTokenDto getToken(Long chatId) throws AppException;
}
