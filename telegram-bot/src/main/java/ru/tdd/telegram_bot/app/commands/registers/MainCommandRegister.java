package ru.tdd.telegram_bot.app.commands.registers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.main.RegisterCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.main.StartCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.main.profile.ProfileCommandHandler;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Реестр основных команд
 */
@Component
public class MainCommandRegister implements CommandRegister {

    private final StartCommandHandler startCommandHandler;

    private final RegisterCommandHandler registerCommandHandler;

    private final ProfileCommandHandler profileCommandHandler;

    @Autowired
    public MainCommandRegister(
            StartCommandHandler startCommandHandler,
            RegisterCommandHandler registerCommandHandler,
            ProfileCommandHandler profileCommandHandler
    ) {
        this.startCommandHandler = startCommandHandler;
        this.registerCommandHandler = registerCommandHandler;
        this.profileCommandHandler = profileCommandHandler;
    }


    @Override
    public List<CommandHandler> getAllHandlers() {
         return List.of(startCommandHandler, registerCommandHandler, profileCommandHandler);
    }
}
