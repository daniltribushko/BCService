package ru.tdd.bc.openapi.tags;

import io.swagger.v3.oas.models.tags.Tag;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Тэги для работы с авторам книг
 */
public class AuthorTags {

    private AuthorTags() {}

    public static final Tag AUTHOR_TAG = new Tag()
            .name("Author Controller")
            .description("Эндпоинты для работы с авторами");
}
