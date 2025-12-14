package ru.tdd.telegram_bot.controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.users.UserDTO;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Конфигурация редис
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, BotCommandDTO> botCommandRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, BotCommandDTO> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<BotCommandDTO> serializer =
                new Jackson2JsonRedisSerializer<>(BotCommandDTO.class);


        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, UserDTO> userRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        Jackson2JsonRedisSerializer<UserDTO> serializer =
                new Jackson2JsonRedisSerializer<>(UserDTO.class);

        RedisTemplate<String, UserDTO> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, Object> simpleRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
