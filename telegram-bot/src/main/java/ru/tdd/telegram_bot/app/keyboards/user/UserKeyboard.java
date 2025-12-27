package ru.tdd.telegram_bot.app.keyboards.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.tdd.telegram_bot.app.keyboards.Keyboard;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.MainBotCommand;
import ru.tdd.telegram_bot.model.enums.Role;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Пользовательская клавиатура
 */
@Component
public class UserKeyboard implements Keyboard {

    @Value("${telegram.bot.keyboards.reply-markup.max-buttons-in-row}")
    private Integer maxButtonsInRow;

    @Override
    public ReplyKeyboard keyboard() {
        List<BotCommand> userCommands = MainBotCommand.getCommandsByRole(Role.USER);
        return new ReplyKeyboardMarkup(
                userCommands
                        .stream()
                        .collect(Collectors.groupingBy(
                                        c -> userCommands.indexOf(c) / maxButtonsInRow
                                )
                        ).values().stream()
                        .map(sublist ->
                                sublist.stream()
                                        .map(c -> new KeyboardButton(c.getText()))
                                        .toList()
                        ).map(KeyboardRow::new)
                        .toList()
        );
    }
}
