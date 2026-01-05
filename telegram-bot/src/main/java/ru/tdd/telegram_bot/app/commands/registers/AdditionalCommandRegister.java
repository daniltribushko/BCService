package ru.tdd.telegram_bot.app.commands.registers;

import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.additional.RegisterAddBirthdayCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.additional.RegisterAddUsernameCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.additional.profile.UpdateProfileSetBirthdayCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.additional.profile.UpdateProfileSetUsernameCommandHandler;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Реестр дополнительных комманд
 */
@Component
public class AdditionalCommandRegister implements CommandRegister {

    private final RegisterAddUsernameCommandHandler registerAddUsernameCommandHandler;

    private final RegisterAddBirthdayCommandHandler registerAddBirthdayCommandHandler;

    private final UpdateProfileSetUsernameCommandHandler updateProfileSetUsernameCommandHandler;

    private final UpdateProfileSetBirthdayCommandHandler updateProfileSetBirthdayCommandHandler;

    public AdditionalCommandRegister(
            RegisterAddUsernameCommandHandler registerAddUsernameCommandHandler,
            RegisterAddBirthdayCommandHandler registerAddBirthdayCommandHandler,
            UpdateProfileSetUsernameCommandHandler updateProfileSetUsernameCommandHandler,
            UpdateProfileSetBirthdayCommandHandler updateProfileSetBirthdayCommandHandler
    ) {
        this.registerAddUsernameCommandHandler = registerAddUsernameCommandHandler;
        this.registerAddBirthdayCommandHandler = registerAddBirthdayCommandHandler;
        this.updateProfileSetUsernameCommandHandler = updateProfileSetUsernameCommandHandler;
        this.updateProfileSetBirthdayCommandHandler = updateProfileSetBirthdayCommandHandler;
    }

    @Override
    public List<CommandHandler> getAllHandlers() {
        return List.of(
                registerAddUsernameCommandHandler,
                registerAddBirthdayCommandHandler,
                updateProfileSetUsernameCommandHandler,
                updateProfileSetBirthdayCommandHandler
        );
    }
}
