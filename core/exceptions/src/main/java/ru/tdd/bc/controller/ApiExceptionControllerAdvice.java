package ru.tdd.bc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.http.ApiException;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Обработчик ошибок api приложения
 */
@RestControllerAdvice
public class ApiExceptionControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionDto> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ExceptionDto(ex.getStatusCode(), ex.getMessage(), ex.getTimestamp()));
    }
}
