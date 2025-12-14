package ru.tdd.telegram_bot.app.commands.handlers.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tdd.telegram_bot.app.commands.handlers.MainCommandHandler;
import ru.tdd.telegram_bot.app.keyboards.guest.GuestKeyboard;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.dto.BooleanDTO;
import ru.tdd.telegram_bot.model.enums.MainBotCommand;
import ru.tdd.telegram_bot.app.utils.URLUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Обработчик стартовой команды бота
 */
@Component
public class StartCommandHandler implements MainCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(StartCommandHandler.class);

    @Value("${services.gateway-port}")
    private Integer gatewayPort;

    @Value("${services.user.host}")
    private String userServiceHost;

    @Value("${services.user.name}")
    private String userServiceName;

    private final GuestKeyboard keyboard;

    @Autowired
    public StartCommandHandler(GuestKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public void handle(TelegramLongPollingBot bot, Message message) {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ) {

            HttpRequest checkUserExistsReq = HttpRequest.newBuilder()
                    .uri(
                            new URI(
                                    URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                            .addPathPart(userServiceName)
                                            .addPathPart(message.getChatId())
                                            .addPathPart("exists")
                                            .build()
                            )
                    )
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(checkUserExistsReq, HttpResponse.BodyHandlers.ofString());

            BooleanDTO dto = new ObjectMapper().readValue(response.body(), BooleanDTO.class);

            if (!dto.getResult()) {
                bot.execute(
                        SendMessage.builder()
                                .chatId(message.getChatId())
                                .replyMarkup(keyboard.keyboard())
                                .text("Выберите команду")
                                .build()
                );
            }

        } catch (URISyntaxException | IOException | InterruptedException | TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public BotCommand commandForHandle() {
        return MainBotCommand.START;
    }
}
