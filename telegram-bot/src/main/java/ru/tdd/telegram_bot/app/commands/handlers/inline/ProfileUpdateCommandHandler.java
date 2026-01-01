package ru.tdd.telegram_bot.app.commands.handlers.inline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.tdd.telegram_bot.app.commands.handlers.InlineBotCommandHandler;
import ru.tdd.telegram_bot.app.keyboards.profile.UpdateProfileKeyboard;
import ru.tdd.telegram_bot.app.utils.TelegramUtils;
import ru.tdd.telegram_bot.model.enums.BotCommand;
import ru.tdd.telegram_bot.model.enums.inline.ProfileCommand;

/**
 * @author Tribushko Danil
 * @since 29.12.2025
 * Обработчик команды обновления профиля
 */
@Component
public class ProfileUpdateCommandHandler implements InlineBotCommandHandler {

    private final UpdateProfileKeyboard updateProfileKeyboard;

    @Autowired
    public ProfileUpdateCommandHandler(
            UpdateProfileKeyboard updateProfileKeyboard
    ) {
        this.updateProfileKeyboard = updateProfileKeyboard;
    }

    @Override
    public BotCommand commandForHandle() {
        return ProfileCommand.UPDATE;
    }

    @Override
    public void handle(TelegramLongPollingBot bot, MaybeInaccessibleMessage message) {
        Long chatId = message.getChatId();
        TelegramUtils.sendBotMessage(
                bot,
                EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(message.getMessageId())
                        .text("Выберите что хотите изменить")
                        .replyMarkup((InlineKeyboardMarkup) updateProfileKeyboard.keyboard())
                        .build()
        );
    }
}
