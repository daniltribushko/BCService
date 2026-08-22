package ru.tdd.bc.book.database.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.tdd.bc.book.database.validations.PastYear;

import java.time.LocalDate;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 */
public class PastValidator implements ConstraintValidator<PastYear, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || LocalDate.now().getYear() >= value;
    }
}
