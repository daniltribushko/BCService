package ru.tdd.telegram_bot.app.commands.handlers.additional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tdd.telegram_bot.app.commands.handlers.AdditionalBotCommandHandler;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.model.dto.BooleanDTO;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.users.SignUpDTO;
import ru.tdd.telegram_bot.model.enums.AdditionalCommand;
import ru.tdd.telegram_bot.model.enums.BotCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик команды получения имени пользователя при регистрации
 */
@Component
public class RegisterAddUsernameCommandHandler implements AdditionalBotCommandHandler {

    private final RedisTemplate<String, BotCommandDTO> redisTemplate;

    @Value("${services.gateway-port}")
    private Integer gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final Logger log;

    @Autowired
    public RegisterAddUsernameCommandHandler(
            RedisTemplate<String, BotCommandDTO> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.log = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message, BotCommandDTO commandDto) {
        if (message.hasText() && Objects.equals(commandForHandle(), commandDto.getCommand())) {
            Long chatId = message.getChatId();
            try {
                SignUpDTO signUpDTO = DtoMapper.fromJson(commandDto.getBody(), SignUpDTO.class);
                String username = message.getText();

                try (HttpClient client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build()){

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(
                                    URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                            .addPathPart(userServiceName)
                                            .addPathPart("exists")
                                            .addQueryParameter("username", username)
                                            .build()
                            ))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    BooleanDTO booleanDTO = DtoMapper.fromJson(response.body(), BooleanDTO.class);

                    if (booleanDTO.getResult()) {
                        bot.execute(
                                SendMessage.builder()
                                        .chatId(chatId)
                                        .text("Пользователь с таким именем уже создан")
                                        .build()
                        );
                    } else {
                        signUpDTO.setUsername(username);
                        commandDto.setCommand(AdditionalCommand.RegisterCommand.ADD_BIRTHDAY);
                        commandDto.setBody(DtoMapper.toJson(signUpDTO));

                        redisTemplate.opsForValue().set(
                                RedisKeysUtils.getBotLastCommandKey(chatId),
                                commandDto,
                                4,
                                TimeUnit.MINUTES
                        );

                        bot.execute(
                                SendMessage.builder()
                                        .text("Введите дату рождения в форматах dd-MM-yyyy")
                                        .chatId(message.getChatId())
                                        .build()
                        );
                    }
                }

            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Override
    public BotCommand commandForHandle() {
        return AdditionalCommand.RegisterCommand.ADD_USERNAME;
    }

}
