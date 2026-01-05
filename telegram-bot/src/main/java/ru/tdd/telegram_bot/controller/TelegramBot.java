package ru.tdd.telegram_bot.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.tdd.telegram_bot.app.commands.BotCommandFactory;
import ru.tdd.telegram_bot.app.commands.handlers.AdditionalBotCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.InlineBotCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.MainCommandHandler;
import ru.tdd.telegram_bot.app.utils.RedisKeysUtils;
import ru.tdd.telegram_bot.app.utils.TelegramUtils;
import ru.tdd.telegram_bot.controller.config.BotConfig;
import ru.tdd.telegram_bot.controller.redis.CurrentUserRedisService;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.users.UserDTO;
import ru.tdd.telegram_bot.model.enums.BotCommandsUtils;
import ru.tdd.telegram_bot.model.enums.Role;
import ru.tdd.telegram_bot.model.enums.main.MainBotCommand;

import java.util.List;
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

    private final CurrentUserRedisService userRedisService;

    @Autowired
    public TelegramBot(
            BotConfig config,
            BotCommandFactory botCommandFactory,
            RedisTemplate<String, BotCommandDTO> botCommandRedisTemplate,
            CurrentUserRedisService userRedisService
    ) {
        super(config.getToken());
        this.config = config;
        this.botCommandFactory = botCommandFactory;
        this.botCommandRedisTemplate = botCommandRedisTemplate;
        this.userRedisService = userRedisService;
    }

    private void actionFromAdditionalHandler(Message message) {
        BotCommandDTO commandDTO = botCommandRedisTemplate.opsForValue()
                .get(RedisKeysUtils.getBotLastCommandKey(message.getChatId()));

        assert commandDTO != null;
        botCommandFactory.getAdditionalHandlerByCommand(commandDTO.getCommand())
                .ifPresent(handler ->
                        ((AdditionalBotCommandHandler) handler)
                                .handle(this, message, commandDTO)
                );
    }

    private void actionFromInlineCommand(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        BotCommandsUtils.getInlineCommandByText(callbackQuery.getData())
                .ifPresentOrElse(
                        command ->
                                botCommandFactory.getInlineHandlerByCommand(
                                        command
                                ).ifPresent(h ->
                                        ((InlineBotCommandHandler) h)
                                                .handle(this, callbackQuery.getMessage())
                                ),
                        () -> TelegramUtils.sendBotMessage(
                                this,
                                SendMessage.builder()
                                        .chatId(callbackQuery.getMessage().getChatId())
                                        .text("Комманда не найдена")
                                        .build()
                        )
                );
    }

    @Override
    public void onUpdateReceived(Update update) {
        Optional.ofNullable(update.getMessage()).ifPresentOrElse(message -> {
                    if (message.hasText()) {
                        List<Role> roles;
                        roles = userRedisService.getUserWithoutException(message.getChatId())
                                .map(UserDTO::getRoles).orElse(List.of(Role.GUEST));
                        MainBotCommand.valueOfOpt(roles, message.getText()).flatMap(botCommandFactory::getMainHandlerByCommand
                        ).ifPresentOrElse(handler -> ((MainCommandHandler) handler).handle(this, message),
                                () -> {
                                    if (
                                            botCommandRedisTemplate.hasKey(
                                                    RedisKeysUtils.getBotLastCommandKey(message.getChatId())
                                            )
                                    )
                                        actionFromAdditionalHandler(message);
                                    else {
                                        TelegramUtils.sendBotMessage(
                                                this,
                                                SendMessage.builder()
                                                        .chatId(message.getChatId())
                                                        .text("Команда не найдена")
                                                        .build()
                                        );
                                    }
                                }
                        );
                    }
                },
                () -> {
                    if (update.hasCallbackQuery())
                        actionFromInlineCommand(update);
                }
        );
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
