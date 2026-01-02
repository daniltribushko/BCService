package ru.tdd.telegram_bot.app.commands.handlers.inline;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tdd.telegram_bot.app.commands.handlers.InlineBotCommandHandler;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.controller.redis.BotCommandRedisService;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.additional.AdditionalUpdateProfile;
import ru.tdd.telegram_bot.model.enums.inline.UpdateProfileCommand;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Обработчик команды обновления имени пользователя
 */
@Component
public class ProfileUpdateUsernameCommandHandler implements InlineBotCommandHandler {

    private final BotCommandRedisService botCommandRedisService;

    public ProfileUpdateUsernameCommandHandler(
            BotCommandRedisService botCommandRedisService
    ) {
        this.botCommandRedisService = botCommandRedisService;
    }

    @Override
    public BotCommand commandForHandle() {
        return UpdateProfileCommand.UPDATE_USERNAME;
    }

    @Override
    public void handle(TelegramLongPollingBot bot, MaybeInaccessibleMessage message) {
        try {
            Long chatId = message.getChatId();

            BotCommandDTO botCommandDTO = new BotCommandDTO(
                    AdditionalUpdateProfile.SET_USERNAME
            );

            bot.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text("Введите новое имя")
                            .build()
            );

            botCommandRedisService.setCommand(
                    chatId,
                    botCommandDTO
            );
        } catch (TelegramApiException e) {
            throw new SimpleRuntimeException(e.getMessage());
        }
    }
}
