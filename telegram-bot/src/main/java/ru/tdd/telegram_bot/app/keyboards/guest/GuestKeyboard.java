package ru.tdd.telegram_bot.app.keyboards.guest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.tdd.telegram_bot.app.keyboards.Keyboard;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.Role;
import ru.tdd.telegram_bot.model.enums.main.MainBotCommand;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Гостевая клавиатура
 */
@Component
public class GuestKeyboard implements Keyboard {

    @Value("${telegram.bot.keyboards.reply-markup.max-buttons-in-row}")
    private Integer maxButtonsInRow;

    @Override
    public ReplyKeyboardMarkup keyboard() {
        List<BotCommand> commands = MainBotCommand.getCommandsByRole(Role.GUEST);

        return new ReplyKeyboardMarkup(commands.stream()
                .collect(Collectors.groupingBy(
                                s -> commands.indexOf(s) / maxButtonsInRow,
                                Collectors.toList()
                        )
                ).values()
                .stream().map(sublist -> new KeyboardRow(
                                sublist.stream().map(command ->
                                        new KeyboardButton(command.getText())
                                ).toList()
                        )
                ).toList());

    }
}
