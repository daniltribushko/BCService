package ru.tdd.telegram_bot.app.commands.handlers.additional.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tdd.telegram_bot.app.commands.handlers.AdditionalBotCommandHandler;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.app.utils.TelegramUtils;
import ru.tdd.telegram_bot.controller.redis.CurrentUserRedisService;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.users.UpdateUserDTO;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.additional.AdditionalUpdateProfile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Tribushko Danil
 * @since 31.12.2025
 * Обработчик команды обновления дня рождения пользователя
 */
@Component
public class UpdateProfileSetBirthdayCommandHandler implements AdditionalBotCommandHandler {

    private final ProfileApiClient profileApiClient;

    private final CurrentUserRedisService currentUserRedisService;

    @Autowired
    public UpdateProfileSetBirthdayCommandHandler(
            ProfileApiClient profileApiClient,
            CurrentUserRedisService currentUserRedisService
    ) {
        this.profileApiClient = profileApiClient;
        this.currentUserRedisService = currentUserRedisService;
    }

    private Optional<String> validateDateString(String dateString) {
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
        return pattern.matcher(dateString).find() ?
                Optional.empty() :
                Optional.of("Дата должна быть формата dd-MM-yyyy");
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message, BotCommandDTO commandDto) {
        Long chatId = message.getChatId();
        String dateString = message.getText();

        validateDateString(dateString).ifPresentOrElse(
                errorText ->
                        TelegramUtils.sendBotMessage(
                                bot,
                                SendMessage.builder()
                                        .chatId(chatId)
                                        .text(errorText)
                                        .build()
                        ),
                () -> {
                    LocalDate newDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    if (newDate.isAfter(LocalDate.now()))
                        TelegramUtils.sendBotMessage(
                                bot,
                                SendMessage.builder()
                                        .chatId(chatId)
                                        .text("Дата рождения должна быть в прошедщем времени")
                                        .build()
                        );
                    else {
                        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
                        updateUserDTO.setBirthday(newDate);

                        try {
                            currentUserRedisService.getUser(chatId).ifPresent(currentUser -> {
                                try {
                                    profileApiClient.sendUpdateUserRequest(
                                            chatId,
                                            currentUser,
                                            updateUserDTO
                                    );
                                    currentUser.setBirthday(newDate);
                                    currentUserRedisService.setUser(chatId, currentUser);

                                    TelegramUtils.sendBotMessage(
                                            bot,
                                            SendMessage.builder()
                                                    .text("Дата рождения успешно обновлена")
                                                    .chatId(chatId)
                                                    .build()
                                    );
                                } catch (AppException e) {
                                    TelegramUtils.sendBotMessage(
                                            bot,
                                            SendMessage.builder()
                                                    .chatId(chatId)
                                                    .text(e.getMessage())
                                                    .build()
                                            );
                                }
                            });
                        } catch (AppException e) {
                            throw new SimpleRuntimeException(e.getMessage());
                        }
                    }
                }
        );
    }

    @Override
    public BotCommand commandForHandle() {
        return AdditionalUpdateProfile.SET_BIRTHDAY;
    }
}
