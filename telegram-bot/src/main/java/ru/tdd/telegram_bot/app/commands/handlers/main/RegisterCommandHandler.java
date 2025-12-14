package ru.tdd.telegram_bot.app.commands.handlers.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tdd.telegram_bot.app.commands.handlers.MainCommandHandler;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.model.enums.AdditionalCommand;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.users.SignUpDTO;
import ru.tdd.telegram_bot.model.enums.MainBotCommand;

import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик команды регистрации
 */
@Component
public class RegisterCommandHandler implements MainCommandHandler {

    private final Logger log;

    private final RedisTemplate<String, BotCommandDTO> redisTemplate;

    public RegisterCommandHandler(
            RedisTemplate<String, BotCommandDTO> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message) {
        Long chatId = message.getChatId();
        try {
            bot.execute(
                    SendMessage.builder()
                            .text("Введите имя пользователя")
                            .chatId(chatId)
                            .build()
            );
            redisTemplate.opsForValue().set(
                    RedisKeysUtils.getBotLastCommandKey(chatId),
                    new BotCommandDTO(
                            AdditionalCommand.RegisterCommand.ADD_USERNAME,
                            DtoMapper.toJson(
                                    new SignUpDTO(
                                            chatId
                                    )
                            )
                    ),
                    4,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException ex) {
            try {
                bot.execute(
                        SendMessage.builder()
                                .chatId(chatId)
                                .text("Произошла ошибка, повторите еще раз")
                                .build()
                );
                log.error(ex.getMessage());
            } catch (TelegramApiException tgEx) {
                log.error(tgEx.getMessage());
            }
        } catch (TelegramApiException tgEx) {
            log.error(tgEx.getMessage());
        }
    }

    @Override
    public BotCommand commandForHandle() {
        return MainBotCommand.REGISTER;
    }
}
