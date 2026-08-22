package ru.tdd.bc.book.controller.open_api.annotations;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tribushko Danil
 * @since 17.08.2026
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Parameter(name = "end_birthday", description = "Максимальная дата рождения", in = ParameterIn.QUERY)
public @interface EndBirthdayQueryParameter {
}
