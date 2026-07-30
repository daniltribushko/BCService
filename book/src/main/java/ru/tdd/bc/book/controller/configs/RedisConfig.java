package ru.tdd.bc.book.controller.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.tdd.bc.book.database.entities.Country;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Конфигурация хранилища-redis
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Country> countryRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Country> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
