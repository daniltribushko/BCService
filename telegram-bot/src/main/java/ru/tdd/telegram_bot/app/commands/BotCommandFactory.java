package ru.tdd.telegram_bot.app.commands;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;
import ru.tdd.telegram_bot.app.commands.registers.AdditionalCommandRegister;
import ru.tdd.telegram_bot.app.commands.registers.MainCommandRegister;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.Role;

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

    private Map<Role, Map<BotCommand, CommandHandler>> mainCommands;

    private Map<Role, Map<BotCommand, CommandHandler>> additionalCommands;

    private final MainCommandRegister commandRegister;

    private final AdditionalCommandRegister additionalCommandRegister;

    @Autowired
    public BotCommandFactory(
            MainCommandRegister commandRegister,
            AdditionalCommandRegister additionalCommandRegister
    ) {
        this.commandRegister = commandRegister;
        this.additionalCommandRegister = additionalCommandRegister;
    }

    @PostConstruct
    public void init() {
        mainCommands = commandRegister.getAllHandlers()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                handlers -> handlers
                                        .getValue()
                                        .stream()
                                        .collect(
                                                Collectors.toMap(CommandHandler::commandForHandle, h -> h)
                                        )
                        )
                );

        additionalCommands = additionalCommandRegister.getAllHandlers()
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                handlers -> handlers
                                        .getValue()
                                        .stream()
                                        .collect(
                                                Collectors.toMap(CommandHandler::commandForHandle, h -> h)
                                        )
                        )
                );
    }

    /**
     * Получить обработчик главной команды
     * @param role роль доступа команды
     * @param command команда бота
     */
    public Optional<CommandHandler> getMainHandlerByCommand(Role role, BotCommand command) {
        return Optional.ofNullable(mainCommands.get(role))
                .map(handlersByRole -> handlersByRole.get(command));
    }

    /**
     * Получить обработчик дополнительной команды
     * @param role роль доступа команды
     * @param command команда бота
     */
    public Optional<CommandHandler> getAdditionalHandlerByCommand(Role role, BotCommand command) {
        return Optional.ofNullable(additionalCommands.get(role))
                .map(handlersByRole -> handlersByRole.get(command));
    }
}
