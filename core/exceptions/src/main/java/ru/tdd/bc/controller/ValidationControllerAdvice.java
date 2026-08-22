package ru.tdd.bc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tdd.bc.dto.ValidationError;
import ru.tdd.bc.dto.ValidationExceptionDTO;

import java.time.LocalDateTime;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Обработчик ошибок валидации Jakarta
 */
@RestControllerAdvice
public class ValidationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        var errors = ex.getBindingResult().getFieldErrors().stream().map(fieldError ->
                new ValidationError(fieldError.getField(), fieldError.getDefaultMessage())
        ).toList();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                        new ValidationExceptionDTO(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                errors,
                                LocalDateTime.now()
                        )
                );
    }
}
