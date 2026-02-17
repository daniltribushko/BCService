package ru.tdd.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tdd.user.application.models.dto.ExceptionDTO;
import ru.tdd.user.application.models.exceptions.ApiException;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 10.02.2026
 * Обработчик исключений в контроллерах
 */
@Hidden
@RestControllerAdvice
public class ExceptionControllerHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ExceptionDTO> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(
                new ExceptionDTO(
                        exception.getStatusCode(),
                        exception.getMessage(),
                        exception.getTimestamp()
                )
        );
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ExceptionDTO(
                                HttpStatus.NOT_FOUND.value(),
                                exception.getMessage(),
                                LocalDateTime.now()
                        )
                );
    }
}
