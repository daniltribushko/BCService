package ru.tdd.bc.database.validators.annotations;

import jakarta.validation.constraints.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 * Дефолтная аннотация для строк, чей размер ограничен по умолчанию 255 в бд
 */
@Retention(RUNTIME)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Size(max = 255, message = "Максимальная длина 255 символов")
public @interface DefaultSize {
}
