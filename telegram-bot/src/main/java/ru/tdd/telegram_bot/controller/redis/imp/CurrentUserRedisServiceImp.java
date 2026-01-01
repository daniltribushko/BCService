package ru.tdd.telegram_bot.controller.redis.imp;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.controller.redis.CurrentUserRedisService;
import ru.tdd.telegram_bot.controller.redis.JwtTokenRedisService;
import ru.tdd.telegram_bot.model.constants.RedisKeyNames;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.ExceptionDto;
import ru.tdd.telegram_bot.model.dto.users.JwtTokenDto;
import ru.tdd.telegram_bot.model.dto.users.UserDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 */
@Component
public class CurrentUserRedisServiceImp implements CurrentUserRedisService {

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final RedisTemplate<String, UserDTO> userRedisTemplate;

    private final JwtTokenRedisService jwtTokenRedisService;

    public CurrentUserRedisServiceImp(
            RedisTemplate<String, UserDTO> userRedisTemplate,
            JwtTokenRedisService jwtTokenRedisService
    ) {
        this.userRedisTemplate = userRedisTemplate;
        this.jwtTokenRedisService = jwtTokenRedisService;
    }

    private HttpRequest getRequestForGetUser(Long chatId, String token) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(
                        new URI(
                                URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                        .addPathPart(userServiceName)
                                        .addQueryParameter("chat-id", chatId)
                                        .build()
                        )
                )
                .setHeader("Authorization", "Bearer " + token)
                .build();
    }

    private Optional<UserDTO> getUserFromUserService(Long chatId) throws AppException {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ) {
            JwtTokenDto token = jwtTokenRedisService.getToken(chatId);
            HttpRequest request = getRequestForGetUser(chatId, token.getJwt());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.SC_OK) {
                return Optional.ofNullable(DtoMapper.fromJson(response.body(), UserDTO.class));
            } else
                throw new AppException(DtoMapper.fromJson(response.body(), ExceptionDto.class).getMessage());
        } catch (URISyntaxException | IOException e) {
            throw new SimpleRuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }

    @Override
    public void setUser(Long chatId, UserDTO userDTO) {
        userRedisTemplate.opsForValue().set(
                RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.CURRENT_USER),
                userDTO,
                1,
                TimeUnit.HOURS
        );
    }

    @Override
    public Optional<UserDTO> getUser(Long chatId) throws AppException {
        Optional<UserDTO> userOpt = Optional.ofNullable(
                userRedisTemplate.opsForValue()
                        .get(RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.CURRENT_USER)
                        )
        );
        return userOpt.isPresent() ? userOpt : getUserFromUserService(chatId);
    }

    @Override
    public Optional<UserDTO> getUserWithoutException(Long chatId) {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ) {
            JwtTokenDto token = jwtTokenRedisService.getToken(chatId);
            HttpRequest request = getRequestForGetUser(chatId, token.getJwt());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.SC_OK) {
                return Optional.ofNullable(DtoMapper.fromJson(response.body(), UserDTO.class));
            } else
                return Optional.empty();
        } catch (AppException e) {
            return Optional.empty();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long chatId) {
        userRedisTemplate.delete(RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.CURRENT_USER));
    }
}
