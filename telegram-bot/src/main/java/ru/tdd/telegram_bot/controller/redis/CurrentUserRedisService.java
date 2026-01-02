package ru.tdd.telegram_bot.controller.redis;

import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.model.dto.users.UserDTO;

import java.util.Optional;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Сервис для работы с пользователями редис
 */
public interface CurrentUserRedisService {

    /**
     * Установка пользователя в хранилище редис
     * @param chatId идентификатор пользователя в телеграм
     * @param userDTO дто пользователя
     */
    void setUser(Long chatId, UserDTO userDTO);

    /**
     * Получение пользователя из хранилища редис
     */
    Optional<UserDTO> getUser(Long chatId) throws AppException;

    Optional<UserDTO> getUserWithoutException(Long chatId);

    void deleteUser(Long chatId);
}
