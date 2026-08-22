package ru.tdd.bc.database.validators;

import jakarta.validation.ConstraintValidatorContext;
import ru.tdd.bc.dto.ValidationError;

import java.util.Collection;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 * Набор утилит для валидации
 */
public class ValidationUtils {

    private ValidationUtils() {}

    public static void addErrors(Collection<ValidationError> errors, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        errors.forEach(error ->
                context.buildConstraintViolationWithTemplate(error.getText())
                        .addPropertyNode(error.getFieldName())
                        .addConstraintViolation()
        );
    }
}
