package ru.tdd.telegram_bot.app.keyboards.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.tdd.telegram_bot.app.keyboards.Keyboard;
import ru.tdd.telegram_bot.model.enums.inline.UpdateProfileCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Клавиатура для обновления профиля
 */
@Component
public class UpdateProfileKeyboard implements Keyboard {

    @Value("${telegram.bot.keyboards.inline.max-buttons-in-row}")
    private Integer maxButtons;

    @Override
    public ReplyKeyboard keyboard() {
        List<UpdateProfileCommand> commands = Arrays.stream(UpdateProfileCommand.values()).toList();
        return new InlineKeyboardMarkup(
                commands.stream().collect(
                        Collectors.groupingBy(
                                c -> commands.indexOf(c) / maxButtons,
                                Collectors.toList()
                        )
                ).values().stream().map(sublist ->
                        sublist.stream().map(c -> InlineKeyboardButton.builder()
                                .text(c.getName())
                                .callbackData(c.getText())
                                .build()
                        ).toList()
                ).toList()
        );
    }
}
