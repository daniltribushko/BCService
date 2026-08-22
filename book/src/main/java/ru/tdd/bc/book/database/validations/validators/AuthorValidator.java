package ru.tdd.bc.book.database.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.tdd.bc.book.database.entities.Author;
import ru.tdd.bc.book.database.repositories.AuthorRepository;
import ru.tdd.bc.book.database.validations.AuthorValid;

/**
 * @author Tribushko Danil
 * @since 18.08.2026
 */
public class AuthorValidator implements ConstraintValidator<AuthorValid, Author> {

    private final AuthorRepository authorRepository;

    public AuthorValidator(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public boolean isValid(Author value, ConstraintValidatorContext context) {
        return false;
    }
}
