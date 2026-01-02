package ru.tdd.telegram_bot.model.enums.inline;

import ru.tdd.telegram_bot.model.enums.InlineBotCommand;
import ru.tdd.telegram_bot.model.enums.Role;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Внутренние команды для обновления профилей
 */
public enum UpdateProfileCommand implements InlineBotCommand {
    UPDATE_USERNAME("ИМЯ", "/profile_update_username", Role.USER),
    UPDATE_BIRTHDAY("ДЕНЬ РОЖДЕНИЯ","/profile_update_birthday", Role.USER);

    private final String name;

    private final String text;

    private final Role role;

    UpdateProfileCommand(String name, String text, Role role) {
        this.name = name;
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

    @Override
    public String getName() {
        return name;
    }
}