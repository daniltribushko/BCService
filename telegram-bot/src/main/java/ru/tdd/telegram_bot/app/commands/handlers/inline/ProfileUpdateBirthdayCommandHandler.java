package ru.tdd.telegram_bot.app.commands.handlers.inline;

import org.springframework.beans.factory.annotation.Autowired;
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
 * @since 31.12.2025
 * Обработчик команды обновления даты рождения
 */
@Component
public class ProfileUpdateBirthdayCommandHandler implements InlineBotCommandHandler {

    private final BotCommandRedisService botCommandRedisService;

    @Autowired
    public ProfileUpdateBirthdayCommandHandler(BotCommandRedisService botCommandRedisService) {
        this.botCommandRedisService = botCommandRedisService;
    }

    @Override
    public void handle(TelegramLongPollingBot bot, MaybeInaccessibleMessage message) {
        try {
            Long chatId = message.getChatId();

            BotCommandDTO botCommandDTO = new BotCommandDTO(
                    AdditionalUpdateProfile.SET_BIRTHDAY
            );

            bot.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text("Введите дату рождения в формате dd-MM-yyyy")
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

    @Override
    public BotCommand commandForHandle() {
        return UpdateProfileCommand.UPDATE_BIRTHDAY;
    }
}
