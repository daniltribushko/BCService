package ru.tdd.geo.controller.aspect;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tdd.geo.application.models.dto.ExceptionDTO;
import ru.tdd.geo.application.models.exceptions.ApiException;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Обработчики исключение в контроллерах
 */
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionDTO> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ExceptionDTO(ex.getStatusCode(), ex.getMessage()));
    }
}
