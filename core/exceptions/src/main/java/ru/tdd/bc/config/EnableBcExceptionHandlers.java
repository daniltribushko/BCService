package ru.tdd.bc.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Аннотация для включения перехватки исключений
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ExceptionHandleConfig.class)
public @interface EnableBcExceptionHandlers {
}
