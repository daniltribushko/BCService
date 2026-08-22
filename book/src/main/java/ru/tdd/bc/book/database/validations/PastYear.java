package ru.tdd.bc.book.database.validations;

import jakarta.validation.Constraint;
import ru.tdd.bc.book.database.validations.validators.PastValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ FIELD })
@Constraint(validatedBy = PastValidator.class)
public @interface PastYear {
}
