package ru.tdd.bc.security.jwt;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Аннотация для включения защиты jwt токена
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(JwtSecurityConfig.class)
public @interface EnableBcJwtSecurity {
}
