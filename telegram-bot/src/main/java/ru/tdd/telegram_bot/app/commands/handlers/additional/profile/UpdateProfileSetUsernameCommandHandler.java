package ru.tdd.telegram_bot.app.commands.handlers.additional.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tdd.telegram_bot.app.commands.handlers.AdditionalBotCommandHandler;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.app.utils.TelegramUtils;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.controller.redis.BotCommandRedisService;
import ru.tdd.telegram_bot.controller.redis.CurrentUserRedisService;
import ru.tdd.telegram_bot.model.dto.BooleanDTO;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.users.UpdateUserDTO;
import ru.tdd.telegram_bot.model.dto.users.UserDTO;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.additional.AdditionalUpdateProfile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Tribushko Danil
 * @since 31.12.2025
 * Обработчик команды обновления имени пользователя
 */
@Component
public class UpdateProfileSetUsernameCommandHandler implements AdditionalBotCommandHandler {

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final CurrentUserRedisService currentUserRedisService;

    private final BotCommandRedisService botCommandRedisService;

    private final ProfileApiClient profileApiClient;

    public UpdateProfileSetUsernameCommandHandler(
            CurrentUserRedisService currentUserRedisService,
            ProfileApiClient profileApiClient,
            BotCommandRedisService botCommandRedisService
    ) {
        this.currentUserRedisService = currentUserRedisService;
        this.profileApiClient = profileApiClient;
        this.botCommandRedisService = botCommandRedisService;
    }

    private boolean isUserExistByUsername(
            String username
    ) {
        try (
                HttpClient client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build()
        ) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(
                            URI.create(
                                    URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                            .addPathPart(userServiceName)
                                            .addPathPart("exists")
                                            .addQueryParameter("username", username)
                                            .build()
                            )
                    )
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 && DtoMapper.fromJson(response.body(), BooleanDTO.class).getResult();
        } catch (IOException e) {
            throw new SimpleRuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message, BotCommandDTO commandDto) {
        Long chatId = message.getChatId();
        String username = message.getText();

        try {
            currentUserRedisService.getUser(chatId).ifPresent(currentUser -> {
                        try {
                            if (!isUserExistByUsername(username)) {
                                UpdateUserDTO updateUserDTO = new UpdateUserDTO();
                                updateUserDTO.setUsername(username);

                                UserDTO newUser = profileApiClient.sendUpdateUserRequest(
                                        chatId,
                                        currentUser,
                                        updateUserDTO
                                );

                                TelegramUtils.sendBotMessage(
                                        bot,
                                        SendMessage.builder()
                                                .chatId(chatId)
                                                .text("Имя успешно обновлено")
                                                .build()
                                );

                                currentUserRedisService.setUser(chatId, newUser);
                                botCommandRedisService.delete(chatId);
                            } else
                                bot.execute(
                                        SendMessage.builder()
                                                .chatId(chatId)
                                                .text("Имя занято")
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
                        } catch (TelegramApiException e) {
                            throw new SimpleRuntimeException(e.getMessage());
                        }
                    }

            );
        } catch (AppException e) {
            throw new SimpleRuntimeException(e.getMessage());
        }

    }

    @Override
    public BotCommand commandForHandle() {
        return AdditionalUpdateProfile.SET_USERNAME;
    }
}
