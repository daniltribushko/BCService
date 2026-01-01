package ru.tdd.telegram_bot.model.enums;

import ru.tdd.telegram_bot.model.enums.additional.AdditionalUpdateProfile;
import ru.tdd.telegram_bot.model.enums.additional.RegisterCommand;
import ru.tdd.telegram_bot.model.enums.inline.ProfileCommand;
import ru.tdd.telegram_bot.model.enums.inline.UpdateProfileCommand;
import ru.tdd.telegram_bot.model.enums.main.MainBotCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Хранилище встоенных команд
 */
public interface BotCommandsUtils {

    static List<InlineBotCommand> getAllInlineCommands() {
        return Stream.concat(
                        Arrays.stream(UpdateProfileCommand.values()),
                        Arrays.stream(ProfileCommand.values())
                )
                .map(c -> (InlineBotCommand) c)
                .toList();
    }

    static Optional<InlineBotCommand> getInlineCommandByText(String text) {
        return getAllInlineCommands()
                .stream()
                .filter(c -> Objects.equals(c.getText(), text))
                .findFirst();
    }

    static Optional<BotCommand> valueOfOpt(String value) {
        return Stream.of(
                        Arrays.stream(MainBotCommand.values()),
                        Arrays.stream(ProfileCommand.values()),
                        Arrays.stream(UpdateProfileCommand.values()),
                        Arrays.stream(RegisterCommand.values()),
                        Arrays.stream(AdditionalUpdateProfile.values())
                ).flatMap(s -> s)
                .filter(c -> c.getText().equals(value))
                .map(c -> (BotCommand) c)
                .findFirst();
    }
}
