package ru.tdd.user.application.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tribushko Danil
 * @since 01.02.2026
 * DTO jwt токена
 */
public class JwtTokenDTO {

    @Schema(
            name = "token",
            description = "Jwt токен пользователя",
            type = "string"
    )
    private String token;

    @Schema(
            name = "expiration",
            description = "Время истечения токена",
            type = "string"
    )
    private long expiration;

    public JwtTokenDTO() {}

    public JwtTokenDTO(String token, long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
