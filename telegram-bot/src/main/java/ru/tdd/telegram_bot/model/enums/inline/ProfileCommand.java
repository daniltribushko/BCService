package ru.tdd.telegram_bot.model.enums.inline;

import ru.tdd.telegram_bot.model.enums.InlineBotCommand;
import ru.tdd.telegram_bot.model.enums.Role;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Дополнительные команды для профиля пользователя
 */
public enum ProfileCommand implements InlineBotCommand {

    /** Обновление профиля  */
    UPDATE("Обновить", "/profile_update", Role.USER),

    /** Удаление профиля */
    DELETE("Удалить","/profile_delete", Role.USER);

    private final String name;

    private final String text;

    private final Role role;

    ProfileCommand(String name, String text, Role role) {
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
