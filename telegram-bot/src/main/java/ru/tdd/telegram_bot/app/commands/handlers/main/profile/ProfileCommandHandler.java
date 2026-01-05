package ru.tdd.telegram_bot.app.commands.handlers.main.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tdd.telegram_bot.app.commands.handlers.MainCommandHandler;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.keyboards.profile.ProfileKeyboard;
import ru.tdd.telegram_bot.app.utils.TelegramUtils;
import ru.tdd.telegram_bot.controller.redis.CurrentUserRedisService;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.Role;
import ru.tdd.telegram_bot.model.enums.main.MainBotCommand;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик команды /profile
 */
@Component
public class ProfileCommandHandler implements MainCommandHandler {

    private static final String SUCCESS_TEXT_MESSAGE = """
                                                            Ваш профиль:
                                                            \tИмя: %s
                                                            \tРоль: %s
                                                            \tДата рождения: %s
                                                            \tДата регистрации: %s
                                                            """;

    private final CurrentUserRedisService userRedisService;

    private final ProfileKeyboard profileKeyboard;

    @Autowired
    public ProfileCommandHandler(
            CurrentUserRedisService userRedisService,
            ProfileKeyboard profileKeyboard
    ) {
        this.userRedisService = userRedisService;
        this.profileKeyboard = profileKeyboard;
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
                                                    SUCCESS_TEXT_MESSAGE
                                                    ,
                                                    userDto.getUsername(),
                                                    String.join(
                                                            ", ",
                                                            userDto.getRoles()
                                                                    .stream()
                                                                    .map(Role::name)
                                                                    .toList()
                                                    ),
                                                    userDto.getBirthday(),
                                                    userDto.getCreationTime()
                                            )
                                    )
                                    .replyMarkup(profileKeyboard.keyboard())
                                    .build()
                    )
            );
        } catch (AppException e) {
            TelegramUtils.sendBotMessage(
                    bot,
                    SendMessage.builder()
                            .text(e.getMessage())
                            .chatId(chatId)
                            .build()
            );
        }
    }

    @Override
    public BotCommand commandForHandle() {
        return MainBotCommand.PROFILE;
    }
}
