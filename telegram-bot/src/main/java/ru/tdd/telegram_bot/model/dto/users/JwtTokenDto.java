package ru.tdd.telegram_bot.model.dto.users;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Dto jwt-токена
 */
public class JwtTokenDto {

    private String jwt;

    public JwtTokenDto() {}

    public JwtTokenDto(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
