package ru.tdd.telegram_bot.controller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.tdd.telegram_bot.model.dto.BotCommandDTO;
import ru.tdd.telegram_bot.model.dto.users.JwtTokenDto;
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        RedisTemplate<String, BotCommandDTO> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<BotCommandDTO> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, BotCommandDTO.class);


        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, UserDTO> userRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<UserDTO> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, UserDTO.class);

        RedisTemplate<String, UserDTO> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, JwtTokenDto> simpleRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, JwtTokenDto> template = new RedisTemplate<>();

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<JwtTokenDto> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, JwtTokenDto.class);

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }
}
