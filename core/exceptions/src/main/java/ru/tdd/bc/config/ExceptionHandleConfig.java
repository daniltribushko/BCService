package ru.tdd.bc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tdd.bc.controller.ApiExceptionControllerAdvice;
import ru.tdd.bc.controller.ValidationControllerAdvice;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Настрока перехватки ошибок приложения
 */
@Configuration
public class ExceptionHandleConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ApiExceptionControllerAdvice controllerAdvice() {
        return new ApiExceptionControllerAdvice();
    }

    @Bean
    public ValidationControllerAdvice validationControllerAdvice() {
        return new ValidationControllerAdvice(objectMapper);
    }
}
