package ru.tdd.telegram_bot.app.commands.handlers.additional.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.controller.redis.JwtTokenRedisService;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.ExceptionDto;
import ru.tdd.telegram_bot.model.dto.users.JwtTokenDto;
import ru.tdd.telegram_bot.model.dto.users.UpdateUserDTO;
import ru.tdd.telegram_bot.model.dto.users.UserDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Tribushko Danil
 * @since 30.12.2025
 * Клиент для отправки запросов к сервису пользователей
 */
@Component
public class ProfileApiClient {

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final JwtTokenRedisService jwtTokenRedisService;

    ProfileApiClient(
            JwtTokenRedisService jwtTokenRedisService
    ) {
        this.jwtTokenRedisService = jwtTokenRedisService;
    }

    protected UserDTO sendUpdateUserRequest(
            Long chatId,
            UserDTO currentUser,
            UpdateUserDTO updateUserDTO
    ) throws AppException {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ) {
            JwtTokenDto token = jwtTokenRedisService.getToken(chatId);


            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(DtoMapper.toJson(updateUserDTO)))
                    .uri(
                            URI.create(
                                    URLUtils.builder(
                                                    userServiceHost + ":" + gatewayPort
                                            )
                                            .addPathPart(userServiceName)
                                            .addPathPart(currentUser.getId())
                                            .build()
                            )
                    )
                    .header("Authorization", "Bearer " + token.getJwt())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
                return DtoMapper.fromJson(response.body(), UserDTO.class);
            else
                throw new AppException(DtoMapper.fromJson(response.body(), ExceptionDto.class).getMessage());
        } catch (IOException e) {
            throw new SimpleRuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }
}
