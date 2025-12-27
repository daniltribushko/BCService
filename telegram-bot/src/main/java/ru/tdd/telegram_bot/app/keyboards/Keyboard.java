package ru.tdd.telegram_bot.app.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Клавиатура телеграм бота
 */
public interface Keyboard {

    ReplyKeyboard keyboard();
}
