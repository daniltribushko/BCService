package ru.tdd.bc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tdd.bc.dto.ExceptionDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Обработчик ошибок валидации Jakarta
 */
@RestControllerAdvice
public class ValidationControllerAdvice {

    private final ObjectMapper objectMapper;

    public ValidationControllerAdvice(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) throws Exception {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                        new ExceptionDto(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                objectMapper.writeValueAsString(errors),
                                LocalDateTime.now()
                        )
                );
    }
}
