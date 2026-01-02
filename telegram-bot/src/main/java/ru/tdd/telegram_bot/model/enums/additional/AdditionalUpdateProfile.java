package ru.tdd.telegram_bot.model.enums.additional;

import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.Role;

/**
 * @author Tribushko Danil
 * @since 31.12.2025
 * Дополнительные команды обновления профиля
 */
public enum AdditionalUpdateProfile implements BotCommand {
    SET_USERNAME("/profile_update_set_username", Role.USER),
    SET_BIRTHDAY("/profile_update_set_birthday", Role.USER);

    private final String text;

    private final Role role;

    AdditionalUpdateProfile(String text, Role role) {
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
