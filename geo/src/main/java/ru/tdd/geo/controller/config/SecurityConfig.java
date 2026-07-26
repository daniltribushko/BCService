package ru.tdd.geo.controller.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.tdd.bc.security.filters.SampleJwtFilter;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * Настройки защиты приложения
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final SampleJwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(
            SampleJwtFilter jwtFilter
    ) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(
                                    "/docs/**",
                                    "/docs/swagger-ui.html"
                                )
                                .permitAll()
                                .anyRequest()
                                .permitAll()
                )
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetails() {
        return (username) -> {
            throw new UnsupportedOperationException();
        };
    }
}
