package ru.tdd.telegram_bot.app.commands.handlers.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tdd.telegram_bot.app.commands.handlers.MainCommandHandler;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.utils.TelegramUtils;
import ru.tdd.telegram_bot.controller.redis.UserRedisService;
import ru.tdd.telegram_bot.model.dto.BaseDTO;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.MainBotCommand;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик команды /profile
 */
@Component
public class ProfileCommandHandler implements MainCommandHandler {

    private final UserRedisService userRedisService;

    private static final Logger log = LoggerFactory.getLogger(ProfileCommandHandler.class);

    @Autowired
    public ProfileCommandHandler(
            UserRedisService userRedisService
    ) {
        this.userRedisService = userRedisService;
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message) {
        Long chatId = message.getChatId();
        try {
            userRedisService.getUser(chatId).ifPresent(userDto ->
                    TelegramUtils.sendBotMessage(
                            bot,
                            SendMessage.builder()
                                    .chatId(chatId)
                                    .text(
                                            String.format(
                                                    """
                                                            Ваш профиль:
                                                            \tИмя: %s
                                                            \tРоль: %s
                                                            \tДата рождения: %s
                                                            \tДата регистрации: %s
                                                            """
                                                    ,
                                                    userDto.getUsername(),
                                                    String.join(
                                                            ", ",
                                                            userDto.getRoles()
                                                                    .stream()
                                                                    .map(BaseDTO::getName)
                                                                    .toList()
                                                    ),
                                                    userDto.getBirthday(),
                                                    userDto.getCreationTime()
                                            )
                                    )
                                    .build(),
                            log
                    )
            );
        } catch (AppException e) {
            TelegramUtils.sendBotMessage(
                    bot,
                    SendMessage.builder()
                            .text(e.getMessage())
                            .chatId(chatId)
                            .build(),
                    log
            );
        }
    }

    @Override
    public BotCommand commandForHandle() {
        return MainBotCommand.PROFILE;
    }
}
