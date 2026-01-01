package ru.tdd.telegram_bot.model.dto.users;

import ru.tdd.telegram_bot.controller.annotations.LocalDateTimeJsonFormat;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Dto jwt-токена
 */
public class JwtTokenDto {

    private String jwt;

    @LocalDateTimeJsonFormat
    private LocalDateTime expirationTime;

    public JwtTokenDto() {}

    public JwtTokenDto(String jwt, LocalDateTime expirationTime) {
        this.jwt = jwt;
        this.expirationTime = expirationTime;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
