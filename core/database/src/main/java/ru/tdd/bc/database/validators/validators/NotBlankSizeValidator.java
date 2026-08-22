package ru.tdd.bc.database.validators.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.tdd.bc.database.validators.ValidationUtils;
import ru.tdd.bc.database.validators.annotations.NotBlankSize;
import ru.tdd.bc.dto.ValidationError;

import java.util.ArrayList;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 * Валидатор строки на заполненность и длину
 */
public class NotBlankSizeValidator implements ConstraintValidator<NotBlankSize, String> {

    private int min;

    private int max;

    private String notBlankMessage;

    private String notLimitSizeMessage;

    @Override
    public void initialize(NotBlankSize constraintAnnotation) {
        if (min > max)
            throw new IllegalArgumentException("Минимальная длина, не может привышать максимальную");
       min = constraintAnnotation.min();
       max = constraintAnnotation.max();
       notBlankMessage = constraintAnnotation.notBlankMessage();
       notLimitSizeMessage = constraintAnnotation.notLimitSizeMessage();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var errors = new ArrayList<ValidationError>(1);
        if (value == null || value.trim().isEmpty())
            errors.add(new ValidationError(notBlankMessage));
        else {
            var length = value.length();

            if (length < min || length > max)
                errors.add(new ValidationError(notLimitSizeMessage));
        }

        ValidationUtils.addErrors(errors, context);

        return errors.isEmpty();
    }
}
