package ru.tdd.author.controller.confing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.tdd.author.application.dto.countries.CountryDTO;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Конфигурация хранилища-redis
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, CountryDTO> countryRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CountryDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
