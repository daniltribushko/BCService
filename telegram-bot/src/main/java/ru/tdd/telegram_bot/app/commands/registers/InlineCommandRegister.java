package ru.tdd.telegram_bot.app.commands.registers;

import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.inline.ProfileDeleteCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.inline.ProfileUpdateBirthdayCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.inline.ProfileUpdateCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.inline.ProfileUpdateUsernameCommandHandler;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Реестр дополнительных встроенных команд
 */
@Component
public class InlineCommandRegister implements CommandRegister {

    private final ProfileUpdateUsernameCommandHandler profileUpdateUsernameCommandHandler;

    private final ProfileUpdateBirthdayCommandHandler profileUpdateBirthdayCommandHandler;

    private final ProfileUpdateCommandHandler profileUpdateCommandHandler;

    private final ProfileDeleteCommandHandler profileDeleteCommandHandler;

    public InlineCommandRegister(
            ProfileUpdateUsernameCommandHandler profileUpdateUsernameCommandHandler,
            ProfileUpdateBirthdayCommandHandler profileUpdateBirthdayCommandHandler,
            ProfileUpdateCommandHandler profileUpdateCommandHandler,
            ProfileDeleteCommandHandler profileDeleteCommandHandler
    ) {
        this.profileUpdateUsernameCommandHandler = profileUpdateUsernameCommandHandler;
        this.profileUpdateCommandHandler = profileUpdateCommandHandler;
        this.profileUpdateBirthdayCommandHandler = profileUpdateBirthdayCommandHandler;
        this.profileDeleteCommandHandler = profileDeleteCommandHandler;
    }

    @Override
    public List<CommandHandler> getAllHandlers() {
        return List.of(
                profileUpdateUsernameCommandHandler,
                profileUpdateCommandHandler,
                profileUpdateBirthdayCommandHandler,
                profileDeleteCommandHandler
        );
    }
}
