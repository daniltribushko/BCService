package ru.tdd.book.controllers.advice;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tdd.core.application.exceptions.ApiException;
import ru.tdd.core.controller.dto.ExceptionDTO;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author Tribushko Danil
 * @since 14.05.2026
 * Обработчик ошибок в контроллерах
 */
@Hidden
@RestControllerAdvice
public class RestControllerExceptionAdvice {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ExceptionDTO> apiException(ApiException apiException) {
        return ResponseEntity.status(apiException.getStatusCode())
                .body(
                        new ExceptionDTO(
                                apiException.getStatusCode().value(),
                                apiException.getMessage(),
                                apiException.getTimestamp()
                        )
                );
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
