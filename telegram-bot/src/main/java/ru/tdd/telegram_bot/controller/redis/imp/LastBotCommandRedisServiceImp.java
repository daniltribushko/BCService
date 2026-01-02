package ru.tdd.telegram_bot.controller.redis.imp;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.controller.redis.BotCommandRedisService;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;

import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Сервис для установки и получения последних комманд бота
 */
@Component
public class LastBotCommandRedisServiceImp implements BotCommandRedisService {

    private final RedisTemplate<String, BotCommandDTO> commandRedisTemplate;

    public LastBotCommandRedisServiceImp(
            RedisTemplate<String, BotCommandDTO> commandRedisTemplate
    ) {
        this.commandRedisTemplate = commandRedisTemplate;
    }

    @Override
    public void setCommand(Long chatId, BotCommandDTO botCommandDTO) {
        commandRedisTemplate.opsForValue()
                .set(
                        RedisKeysUtils.getBotLastCommandKey(chatId),
                        botCommandDTO,
                        4,
                        TimeUnit.MINUTES
                );
    }

    private BotCommandDTO setAndReturnCommand(Long chatId, BotCommandDTO botCommandDTO) {
        setCommand(chatId, botCommandDTO);
        return botCommandDTO;
    }

    @Override
    public BotCommandDTO getCommand(Long chatId, BotCommandDTO botCommandDTO) {
        return
                commandRedisTemplate.hasKey(RedisKeysUtils.getBotLastCommandKey(chatId)) ?
                        commandRedisTemplate.opsForValue().get(RedisKeysUtils.getBotLastCommandKey(chatId)) :
                        setAndReturnCommand(chatId, botCommandDTO);
    }

    @Override
    public void delete(Long chatId) {
        commandRedisTemplate.delete(RedisKeysUtils.getBotLastCommandKey(chatId));
    }
}
