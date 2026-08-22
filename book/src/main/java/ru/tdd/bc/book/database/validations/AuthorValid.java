package ru.tdd.bc.book.database.validations;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;
import ru.tdd.bc.book.database.validations.validators.AuthorValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = AuthorValidator.class)
public @interface AuthorValid {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
