package ru.tdd.bc.http;


import org.springframework.http.HttpStatus;

/**
 * @author Tribushko Danil
 * @since 04.06.2026
 */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
