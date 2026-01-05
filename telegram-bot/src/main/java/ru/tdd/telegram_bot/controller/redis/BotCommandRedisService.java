package ru.tdd.telegram_bot.controller.redis;

import ru.tdd.telegram_bot.model.dto.BotCommandDTO;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Сервис для работы с командами бота в редис
 */
public interface BotCommandRedisService {

    void setCommand(Long chatId, BotCommandDTO botCommandDTO);

    BotCommandDTO getCommand(Long chatId, BotCommandDTO botCommandDTO);

    void delete(Long chatId);
}
