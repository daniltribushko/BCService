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
import ru.tdd.telegram_bot.controller.redis.UserRedisService;
import ru.tdd.telegram_bot.model.constants.RedisKeyNames;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.ExceptionDto;
import ru.tdd.telegram_bot.model.dto.users.UserDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 */
@Component
public class UserRedisServiceImp implements UserRedisService {

    private static final Logger log = LoggerFactory.getLogger(UserRedisServiceImp.class);
    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final RedisTemplate<String, UserDTO> userRedisTemplate;

    private final JwtTokenRedisService jwtTokenRedisService;

    public UserRedisServiceImp(
            RedisTemplate<String, UserDTO> userRedisTemplate,
            JwtTokenRedisService jwtTokenRedisService
    ) {
        this.userRedisTemplate = userRedisTemplate;
        this.jwtTokenRedisService = jwtTokenRedisService;
    }

    private Optional<UserDTO> getUserFromUserService(Long chatId) throws AppException {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ){
            Optional<String> tokenOpt = jwtTokenRedisService.getToken(chatId);
            if (tokenOpt.isPresent()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(
                                new URI(
                                        URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                                .addPathPart(userServiceName)
                                                .addQueryParameter("chat-id", chatId)
                                                .build()
                                )
                        )
                        .setHeader("Authorization", "Bearer " + tokenOpt.get())
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == HttpStatus.SC_OK) {
                    UserDTO userDTO = DtoMapper.fromJson(response.body(), UserDTO.class);
                    return Optional.ofNullable(userDTO);
                } else {
                    ExceptionDto exceptionDto = DtoMapper.fromJson(response.body(), ExceptionDto.class);
                    throw new AppException(exceptionDto.getMessage());
                }
            } else
                return Optional.empty();
        } catch (URISyntaxException | IOException | InterruptedException e) {
          log.error(e.getMessage());
          return Optional.empty();
        }
    }

    @Override
    public void setUser(Long chatId, UserDTO userDTO) {
        userRedisTemplate.opsForValue().set(
                RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.CURRENT_USER),
                userDTO
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
}
