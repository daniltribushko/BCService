package ru.tdd.telegram_bot.controller.redis.imp;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.exceptions.AppException;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 */
@Component
public class JwtTokenRedisServiceImp implements JwtTokenRedisService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenRedisServiceImp.class);

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final RedisTemplate<String, Object> redisTemplate;

    public JwtTokenRedisServiceImp(
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setToken(Long chatId, String token) {
        redisTemplate.opsForValue().set(
                RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.JWT_TOKEN),
                token,
                1,
                TimeUnit.DAYS
        );
    }

    private Optional<String> getTokenFromUserService(Long chatId) throws AppException {
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

            if (response.statusCode() == HttpStatus.SC_OK) {
                JwtTokenDto tokenDto = DtoMapper.fromJson(response.body(), JwtTokenDto.class);
                String jwt = tokenDto.getJwt();

                setToken(chatId, jwt);

                return Optional.ofNullable(jwt);
            } else {
                ExceptionDto exceptionDto = DtoMapper.fromJson(response.body(), ExceptionDto.class);
                throw new AppException(exceptionDto.getMessage());
            }
        } catch (InterruptedException | URISyntaxException | IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getToken(Long chatId) throws AppException {
        Optional<String> tokenOpt = Optional.ofNullable(redisTemplate.opsForValue()
                        .get(RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.JWT_TOKEN)))
                .map(Object::toString);
        return tokenOpt.isPresent() ? tokenOpt : getTokenFromUserService(chatId);
    }
}