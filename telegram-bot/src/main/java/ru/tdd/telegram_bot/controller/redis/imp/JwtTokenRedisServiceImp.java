package ru.tdd.telegram_bot.controller.redis.imp;

import jakarta.annotation.PreDestroy;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.exceptions.AppException;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.controller.redis.JwtTokenRedisService;
import ru.tdd.telegram_bot.model.constants.RedisKeyNames;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.ExceptionDto;
import ru.tdd.telegram_bot.model.dto.users.JwtTokenDto;
import ru.tdd.telegram_bot.model.dto.users.SignInDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 */
@Component
public class JwtTokenRedisServiceImp implements JwtTokenRedisService {

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final RedisTemplate<String, JwtTokenDto> redisTemplate;

    public JwtTokenRedisServiceImp(
            RedisTemplate<String, JwtTokenDto> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @PreDestroy
    public void cleanUp() {
        redisTemplate.execute(connection -> {
            connection.serverCommands().flushAll();
            return null;
        }, true);
    }

    @Override
    public void setToken(Long chatId, JwtTokenDto token) {
        redisTemplate.opsForValue().set(
                RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.JWT_TOKEN),
                token,
                8,
                TimeUnit.HOURS
        );
    }

    private JwtTokenDto getTokenFromUserService(Long chatId) throws AppException {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(
                            new URI(
                                    URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                            .addPathPart(userServiceName)
                                            .addPathPart("sign-in")
                                            .build()
                            )
                    )
                    .POST(HttpRequest.BodyPublishers.ofString(DtoMapper.toJson(new SignInDTO(chatId))))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == HttpStatus.SC_OK) {
                JwtTokenDto tokenDto = DtoMapper.fromJson(response.body(), JwtTokenDto.class);

                setToken(chatId, tokenDto);

                return tokenDto;
            } else if (statusCode >= 400 && statusCode < 500){
                ExceptionDto exceptionDto = DtoMapper.fromJson(response.body(), ExceptionDto.class);
                throw new AppException(exceptionDto.getMessage());
            } else
                throw new AppException(response.body());
        } catch (URISyntaxException | IOException e) {
            throw new SimpleRuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }

    @Override
    public JwtTokenDto getToken(Long chatId) throws AppException {
        Optional<JwtTokenDto> tokenOpt = Optional.ofNullable(redisTemplate.opsForValue()
                .get(
                        RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.JWT_TOKEN))
        ).flatMap(token -> {
            if (token.getExpirationTime().isBefore(LocalDateTime.now()))
                return Optional.of(token);
            else
                return Optional.empty();
        });
        return tokenOpt.orElseGet(() -> getTokenFromUserService(chatId));
    }
}