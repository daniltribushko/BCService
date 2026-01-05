package ru.tdd.telegram_bot.app.commands;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;
import ru.tdd.telegram_bot.app.commands.registers.AdditionalCommandRegister;
import ru.tdd.telegram_bot.app.commands.registers.InlineCommandRegister;
import ru.tdd.telegram_bot.app.commands.registers.MainCommandRegister;
import ru.tdd.telegram_bot.model.enums.BotCommand;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Фабрика для команд бота и их обработчиков
 */
@Component
public class BotCommandFactory {

    private Map<BotCommand, CommandHandler> mainCommands;

    private Map<BotCommand, CommandHandler> additionalCommands;

    private Map<BotCommand, CommandHandler> inlineCommands;

    private final MainCommandRegister commandRegister;

    private final AdditionalCommandRegister additionalCommandRegister;

    private final InlineCommandRegister inlineCommandRegister;

    @Autowired
    public BotCommandFactory(
            MainCommandRegister commandRegister,
            AdditionalCommandRegister additionalCommandRegister,
            InlineCommandRegister inlineCommandRegister
    ) {
        this.commandRegister = commandRegister;
        this.additionalCommandRegister = additionalCommandRegister;
        this.inlineCommandRegister = inlineCommandRegister;
    }

    @PostConstruct
    public void init() {
        mainCommands = commandRegister.getAllHandlers()
                .stream()
                .collect(Collectors.toMap(
                                CommandHandler::commandForHandle,
                                handlers -> handlers
                        )
                );

        additionalCommands = additionalCommandRegister.getAllHandlers()
                .stream()
                .collect(
                        Collectors.toMap(
                                CommandHandler::commandForHandle,
                                handlers -> handlers
                        )
                );

        inlineCommands = inlineCommandRegister.getAllHandlers()
                .stream()
                .collect(
                        Collectors.toMap(
                                CommandHandler::commandForHandle,
                                handlers -> handlers
                        )
                );
    }

    /**
     * Получить обработчик главной команды
     * @param command команда бота
     */
    public Optional<CommandHandler> getMainHandlerByCommand(BotCommand command) {
        return Optional.ofNullable(mainCommands.get(command));
    }

    /**
     * Получить обработчик дополнительной команды
     * @param command команда бота
     */
    public Optional<CommandHandler> getAdditionalHandlerByCommand(BotCommand command) {
        return Optional.ofNullable(additionalCommands.get(command));
    }

    public Optional<CommandHandler> getInlineHandlerByCommand(BotCommand command) {
        return Optional.ofNullable(inlineCommands.get(command));
    }
}
