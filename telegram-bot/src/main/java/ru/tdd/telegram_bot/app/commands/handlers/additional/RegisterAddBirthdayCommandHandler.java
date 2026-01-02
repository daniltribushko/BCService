package ru.tdd.telegram_bot.app.commands.handlers.additional;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tdd.telegram_bot.app.commands.handlers.AdditionalBotCommandHandler;
import ru.tdd.telegram_bot.app.exceptions.SimpleRuntimeException;
import ru.tdd.telegram_bot.app.keyboards.user.UserKeyboard;
import ru.tdd.telegram_bot.app.utils.DateUtils;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.app.utils.URLUtils;
import ru.tdd.telegram_bot.controller.redis.BotCommandRedisService;
import ru.tdd.telegram_bot.model.constants.RedisKeyNames;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.DtoMapper;
import ru.tdd.telegram_bot.model.dto.ExceptionDto;
import ru.tdd.telegram_bot.model.dto.users.JwtTokenDto;
import ru.tdd.telegram_bot.model.dto.users.SignUpDTO;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.additional.RegisterCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final UserKeyboard userKeyboard;

    private final BotCommandRedisService botCommandRedisService;

    private final RedisTemplate<String, JwtTokenDto> simpleRedisTemplate;

    public RegisterAddBirthdayCommandHandler(
            UserKeyboard userKeyboard,
            BotCommandRedisService botCommandRedisService,
            RedisTemplate<String, JwtTokenDto> simpleRedisTemplate
    ) {
        this.userKeyboard = userKeyboard;
        this.botCommandRedisService = botCommandRedisService;
        this.simpleRedisTemplate = simpleRedisTemplate;
    }

    private void registerNewUser(
            TelegramLongPollingBot bot,
            Long chatId,
            BotCommandDTO commandDto,
            LocalDate date
    ) throws JsonProcessingException {
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


                botCommandRedisService.delete(chatId);
                simpleRedisTemplate.opsForValue().set(
                        RedisKeysUtils.getCommandWithChatId(chatId, RedisKeyNames.JWT_TOKEN),
                        tokenDto,
                        DateUtils.DAY,
                        TimeUnit.DAYS
                );

                bot.execute(
                        SendMessage.builder()
                                .chatId(chatId)
                                .text("Регистрация прошла успешно")
                                .replyMarkup(userKeyboard.keyboard())
                                .build()
                );
            } else {
                bot.execute(
                        SendMessage.builder()
                                .chatId(chatId)
                                .text(DtoMapper.fromJson(response.body(), ExceptionDto.class).getMessage())
                                .build()
                );

                botCommandRedisService.delete(chatId);
            }
        } catch (TelegramApiException | IOException | URISyntaxException e) {
            throw new SimpleRuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message, BotCommandDTO commandDto) {
        if (message.hasText() && Objects.equals(commandDto.getCommand(), commandForHandle())) {
            String dateStr = message.getText();

            Pattern pattern = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
            Long chatId = message.getChatId();

            try {
                if (pattern.matcher(dateStr).find()) {
                    LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    if (date.isBefore(LocalDate.now()))
                        registerNewUser(bot, chatId, commandDto, date);
                    else
                        bot.execute(
                                SendMessage.builder()
                                        .chatId(chatId)
                                        .text("Дата рождения должна быть в прошедщем времени")
                                        .build()
                        );
                } else
                    bot.execute(
                            SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Дата должна быть в формате dd-MM-yyyy")
                                    .build()
                    );
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
                throw new SimpleRuntimeException(ex.getMessage());
            }
        }
    }

    @Override
    public BotCommand commandForHandle() {
        return RegisterCommand.ADD_BIRTHDAY;
    }
}
