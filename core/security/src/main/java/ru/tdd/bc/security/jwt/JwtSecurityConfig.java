package ru.tdd.bc.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tdd.bc.security.filters.SampleJwtFilter;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Конфигурация jwt токенов
 */
@Configuration
public class JwtSecurityConfig {

    @Value("jwt.secret")
    private String secretKey;

    @Bean
    public JwtService jwtService() {
        return new SampleJwtService();
    }

    @Bean
    public SampleJwtFilter sampleJwtFilter() {
        return new SampleJwtFilter(jwtService(), secretKey);
    }
}
