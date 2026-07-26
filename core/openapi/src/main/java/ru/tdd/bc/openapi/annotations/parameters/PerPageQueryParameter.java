package ru.tdd.bc.openapi.annotations.parameters;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Аннотация для описания номера страницы с данными
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER, METHOD, FIELD, ANNOTATION_TYPE})
@Parameter(name = "per_page", description = "Номер страницы с данными", in = ParameterIn.QUERY)
public @interface PerPageQueryParameter {
}
