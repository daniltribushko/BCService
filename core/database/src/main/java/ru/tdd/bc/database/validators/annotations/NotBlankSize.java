package ru.tdd.bc.database.validators.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.tdd.bc.database.validators.validators.NotBlankSizeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 * Аннотация для валидации строки на заполненность и длину
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = NotBlankSizeValidator.class)
public @interface NotBlankSize {

    int min() default 0;

    int max() default 255;

    String notBlankMessage() default "Необходимо указать";

    String notLimitSizeMessage() default "Допустимая длина до 255 символов";

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
