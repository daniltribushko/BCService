package ru.tdd.telegram_bot.model.enums.main;

import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.Role;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Основные комманды бота
 */
public enum MainBotCommand implements BotCommand {

    START("/start"),
    REGISTER("/register"),
    PROFILE("/profile", Role.USER);

    private final String text;

    private final Role role;

    MainBotCommand(String text, Role role) {
        this.text = text;
        this.role = role;
    }

    MainBotCommand(String text) {
        this.text = text;
        this.role = Role.GUEST;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Role getRole() {
        return role;
    }

    public static Optional<BotCommand> valueOfOpt(List<Role> roles, String value) {
        return Arrays.stream(values())
                .filter(c -> c.text.equals(value))
                .filter(c -> roles.contains(c.role))
                .map(c -> (BotCommand) c)
                .findFirst();
    }

    public static Optional<BotCommand> valueOfOpt(String value) {
        return Arrays.stream(values())
                .filter(c -> c.text.equals(value))
                .map(c -> (BotCommand) c)
                .findFirst();
    }

    public static List<BotCommand> getCommandsByRole(Role role) {
        return Arrays.stream(values())
                .filter(c -> Objects.equals(role, c.role))
                .map(c -> (BotCommand) c).toList();
    }
}