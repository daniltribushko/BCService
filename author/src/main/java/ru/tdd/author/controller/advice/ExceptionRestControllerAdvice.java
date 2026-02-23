package ru.tdd.author.controller.advice;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tdd.author.application.dto.ExceptionDTO;
import ru.tdd.author.application.exceptions.ApiException;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * Перехватчик исключений
 */
@Hidden
@RestControllerAdvice
public class ExceptionRestControllerAdvice {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ExceptionDTO> apiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ExceptionDTO(ex.getStatusCode(), ex.getMessage(), ex.getTimestamp()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                        new ExceptionDTO(
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                String.join(
                                        "",
                                        Arrays.stream(ex.getDetailMessageArguments())
                                                .map(Object::toString)
                                                .toList()
                                ),
                                LocalDateTime.now()
                        )
                );
    }
}
