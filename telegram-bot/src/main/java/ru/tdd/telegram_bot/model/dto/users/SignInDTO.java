package ru.tdd.telegram_bot.model.dto.users;

/**
 * @author Tribushko Danil
 * @since 20.12.205
 * Dto авторизации пользователей
 */
public class SignInDTO {

    private Long chatId;

    public SignInDTO() {}

    public SignInDTO(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
