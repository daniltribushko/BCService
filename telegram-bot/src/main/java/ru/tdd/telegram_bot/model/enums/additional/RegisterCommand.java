package ru.tdd.telegram_bot.model.enums.additional;

import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.Role;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Дополнительные комманды для регистрации пользователей
 */
public enum RegisterCommand implements BotCommand {

    /** Добавление имени пользователя */
    ADD_USERNAME("/register_add_username", Role.USER),

    /** Добавление даты рождения пользователя */
    ADD_BIRTHDAY("/register_add_birthday", Role.USER);

    private final String text;

    private final Role role;

    RegisterCommand(String text, Role role) {
        this.text = text;
        this.role = role;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Role getRole() {
        return role;
    }
}
