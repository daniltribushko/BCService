package ru.tdd.telegram_bot.controller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.tdd.telegram_bot.controller.factory.YamlPropertySourceFactory;

/**
 * Конфигурация телеграм бота
 */
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:telegram-secret.yaml")
public class BotConfig {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.token}")
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
