package ru.tdd.telegram_bot.app.commands.registers;

import org.springframework.stereotype.Component;
import ru.tdd.telegram_bot.app.commands.handlers.CommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.additional.RegisterAddBirthdayCommandHandler;
import ru.tdd.telegram_bot.app.commands.handlers.additional.RegisterAddUsernameCommandHandler;
import ru.tdd.telegram_bot.model.enums.Role;

import java.util.List;
import java.util.Map;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Реестр дополнительных комманд
 */
@Component
public class AdditionalCommandRegister implements CommandRegister {

    private final RegisterAddUsernameCommandHandler registerAddUsernameCommandHandler;

    private final RegisterAddBirthdayCommandHandler registerAddBirthdayCommandHandler;

    public AdditionalCommandRegister(
            RegisterAddUsernameCommandHandler registerAddUsernameCommandHandler,
            RegisterAddBirthdayCommandHandler registerAddBirthdayCommandHandler
    ) {
        this.registerAddUsernameCommandHandler = registerAddUsernameCommandHandler;
        this.registerAddBirthdayCommandHandler = registerAddBirthdayCommandHandler;
    }

    @Override
    public Map<Role, List<CommandHandler>> getAllHandlers() {
        return Map.of(
                Role.USER, List.of(registerAddUsernameCommandHandler, registerAddBirthdayCommandHandler)
        );
    }
}
