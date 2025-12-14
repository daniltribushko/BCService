package ru.tdd.telegram_bot.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.tdd.telegram_bot.app.commands.BotCommandFactory;
import ru.tdd.telegram_bot.app.commands.handlers.AdditionalBotCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.MainCommandHandler;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.enums.MainBotCommand;
import ru.tdd.telegram_bot.model.enums.Role;
import ru.tdd.telegram_bot.controller.config.BotConfig;

import java.util.Optional;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Телеграм бот
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final BotCommandFactory botCommandFactory;

    private final RedisTemplate<String, BotCommandDTO> botCommandRedisTemplate;

    @Autowired
    public TelegramBot(
            BotConfig config,
            BotCommandFactory botCommandFactory,
            RedisTemplate<String, BotCommandDTO> botCommandRedisTemplate
    ) {
        super(config.getToken());
        this.config = config;
        this.botCommandFactory = botCommandFactory;
        this.botCommandRedisTemplate = botCommandRedisTemplate;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Optional.ofNullable(update.getMessage()).ifPresent(message -> {
            if (message.hasText()) {
                MainBotCommand.valueOfOpt(message.getText()).flatMap(command ->
                        botCommandFactory.getMainHandlerByCommand(Role.USER, command)
                ).ifPresentOrElse(handler -> ((MainCommandHandler) handler).handle(this, message),
                        () -> {
                            if (
                                    botCommandRedisTemplate.hasKey(
                                            RedisKeysUtils.getBotLastCommandKey(message.getChatId())
                                    )
                            ) {
                                BotCommandDTO commandDTO = botCommandRedisTemplate.opsForValue()
                                        .get(RedisKeysUtils.getBotLastCommandKey(message.getChatId()));

                                botCommandFactory.getAdditionalHandlerByCommand(Role.USER, commandDTO.getCommand())
                                        .ifPresent(handler ->
                                                ((AdditionalBotCommandHandler) handler)
                                                        .handle(this, message, commandDTO)
                                        );
                            }
                        }
                );
            }
        });
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        new TelegramBotsApi(DefaultBotSession.class).registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }
}
