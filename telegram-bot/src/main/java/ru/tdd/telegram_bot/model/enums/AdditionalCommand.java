package ru.tdd.telegram_bot.model.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Дополнительные комманды
 */
public interface AdditionalCommand {

    /**
     * @author Tribushko Danil
     * @since 20.12.2025
     * Дополнительные комманды для регистрации пользователей
     */
    enum RegisterCommand implements BotCommand {

        /** Добавление имени пользователя */
        ADD_USERNAME("/register_add_username"),

        /** Добавление даты рождения пользователя */
        ADD_BIRTHDAY("/register_add_birthday");

        private final String text;

        RegisterCommand(String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    static Optional<BotCommand> valueOfOpt(String value) {
        return Arrays.stream(RegisterCommand.values())
                .filter(c -> c.text.equals(value))
                .map(c -> (BotCommand) c)
                .findFirst();
    }
}