package ru.tdd.telegram_bot.app.commands.handlers.additional;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tdd.telegram_bot.app.commands.handlers.AdditionalBotCommandHandler;
import ru.tdd.telegram_bot.app.keyboards.user.UserKeyboard;
import ru.tdd.telegram_bot.app.utils.DateUtils;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.model.constants.RedisKeyNames;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.ExceptionDto;
import ru.tdd.telegram_bot.model.dto.users.JwtTokenDto;
import ru.tdd.telegram_bot.model.dto.users.SignUpDTO;
import ru.tdd.telegram_bot.model.enums.AdditionalCommand;
import ru.tdd.telegram_bot.model.enums.BotCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик команды для получения даты рождения при регистрации
 */
@Component
public class RegisterAddBirthdayCommandHandler implements AdditionalBotCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterAddBirthdayCommandHandler.class);

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final UserKeyboard userKeyboard;

    private final RedisTemplate<String, BotCommandDTO> botCommandRedisTemplate;

    private final RedisTemplate<String, Object> simpleRedisTemplate;

    public RegisterAddBirthdayCommandHandler(
            UserKeyboard userKeyboard,
            RedisTemplate<String, BotCommandDTO> botCommandRedisTemplate,
            RedisTemplate<String, Object> simpleRedisTemplate
    ) {
        this.userKeyboard = userKeyboard;
        this.botCommandRedisTemplate = botCommandRedisTemplate;
        this.simpleRedisTemplate = simpleRedisTemplate;
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message, BotCommandDTO commandDto) {
        if (message.hasText() && Objects.equals(commandDto.getCommand(), commandForHandle())) {
            String dateStr = message.getText();

            Pattern pattern = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}");
            Long chayId = message.getChatId();

            try {
                if (pattern.matcher(dateStr).find()) {
                    LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    if (date.isBefore(LocalDate.now())) {
                        SignUpDTO signUpDTO = DtoMapper.fromJson(commandDto.getBody(), SignUpDTO.class);
                        signUpDTO.setBirthday(date);

                        try (HttpClient client = HttpClient.newBuilder()
                                .followRedirects(HttpClient.Redirect.NORMAL)
                                .build()) {

                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(
                                            new URI(
                                                    URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                                            .addPathPart(userServiceName)
                                                            .addPathPart("sign-up")
                                                            .build()
                                            )
                                    )
                                    .POST(HttpRequest.BodyPublishers.ofString(DtoMapper.toJson(signUpDTO)))
                                    .build();

                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                            
                            if (response.statusCode() == HttpStatus.SC_CREATED) {
                                JwtTokenDto tokenDto = DtoMapper.fromJson(response.body(), JwtTokenDto.class);


                                botCommandRedisTemplate.delete(RedisKeysUtils.getBotLastCommandKey(chayId));
                                simpleRedisTemplate.opsForValue().set(
                                        RedisKeysUtils.getCommandWithChatId(chayId, RedisKeyNames.JWT_TOKEN),
                                        tokenDto.getJwt(),
                                        DateUtils.DAY,
                                        TimeUnit.DAYS
                                );

                                bot.execute(
                                        SendMessage.builder()
                                                .chatId(chayId)
                                                .text("Регистрация прошла успешно")
                                                .replyMarkup(userKeyboard.keyboard())
                                                .build()
                                );
                            } else {
                                ExceptionDto exceptionDto = DtoMapper.fromJson(response.body(), ExceptionDto.class);

                                bot.execute(
                                        SendMessage.builder()
                                                .chatId(chayId)
                                                .text(exceptionDto.getMessage())
                                                .build()
                                );
                            }
                        }

                    } else {
                        bot.execute(
                                SendMessage.builder()
                                        .chatId(chayId)
                                        .text("Дата должна быть в прошедшем времени")
                                        .build()
                        );
                    }
                } else {
                    bot.execute(
                            SendMessage.builder()
                                    .chatId(chayId)
                                    .text("Дата должна быть в формате dd-HH-yyyy")
                                    .build()
                    );
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    @Override
    public BotCommand commandForHandle() {
        return AdditionalCommand.RegisterCommand.ADD_BIRTHDAY;
    }
}
