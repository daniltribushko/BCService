package ru.tdd.telegram_bot.app.commands.handlers.inline;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tdd.telegram_bot.app.commands.handlers.InlineBotCommandHandler;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.app.utils.TelegramUtils;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.controller.redis.CurrentUserRedisService;
import ru.tdd.telegram_bot.controller.redis.JwtTokenRedisService;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.ExceptionDto;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.inline.ProfileCommand;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 01.01.2026
 * Обработчик команды на удаление профиля
 */
@Component
public class ProfileDeleteCommandHandler implements InlineBotCommandHandler {

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final CurrentUserRedisService currentUserRedisService;

    private final JwtTokenRedisService jwtTokenRedisService;

    @Autowired
    public ProfileDeleteCommandHandler(
            CurrentUserRedisService currentUserRedisService,
            JwtTokenRedisService jwtTokenRedisService
    ) {
        this.currentUserRedisService = currentUserRedisService;
        this.jwtTokenRedisService = jwtTokenRedisService;
    }

    private void sendDeleteRequest(TelegramLongPollingBot bot, UUID userId, Long chatId) {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ) {
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(
                            URI.create(
                                    URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                            .addPathPart(userServiceName)
                                            .addPathPart(userId)
                                            .build()
                            )
                    )
                    .setHeader("Authorization", "Bearer " + jwtTokenRedisService.getToken(chatId).getJwt())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == HttpStatus.SC_NO_CONTENT) {
                currentUserRedisService.deleteUser(chatId);
                bot.execute(
                        SendMessage.builder()
                                .chatId(chatId)
                                .text("Ваш профиль успешно удален")
                                .build()
                );
            } else if (statusCode >= HttpStatus.SC_BAD_REQUEST && statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR)
                throw new AppException(DtoMapper.fromJson(response.body(), ExceptionDto.class).getMessage());
            else
                throw new AppException(response.body());
        } catch (IOException | TelegramApiException e) {
            throw new SimpleRuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }

    @Override
    public void handle(TelegramLongPollingBot bot, MaybeInaccessibleMessage message) {
        Long chatId = message.getChatId();
        try {
            currentUserRedisService.getUser(chatId).ifPresent(currentUser ->
                    sendDeleteRequest(bot, currentUser.getId(), chatId)
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
        return ProfileCommand.DELETE;
    }
}
