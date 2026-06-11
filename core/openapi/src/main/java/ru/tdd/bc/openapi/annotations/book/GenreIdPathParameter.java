package ru.tdd.bc.openapi.annotations.book;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Аннотация для описания идентификатора жанра книги в спецификации open-api
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER, METHOD, FIELD, ANNOTATION_TYPE})
@Parameter(name = "genreId", description = "Идентификатор жанра книги", required = true, in = ParameterIn.PATH)
public @interface GenreIdPathParameter {
}
