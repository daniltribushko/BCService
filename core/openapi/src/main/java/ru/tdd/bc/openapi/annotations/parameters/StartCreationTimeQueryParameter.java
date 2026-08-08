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
 * @since 06.08.2026
 * Аннотация для описания минимального времени создания
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER, METHOD, FIELD, ANNOTATION_TYPE})
@Parameter(name = "start_creation_time", description = "Минимальное время создания", in = ParameterIn.QUERY)
public @interface StartCreationTimeQueryParameter {
}
