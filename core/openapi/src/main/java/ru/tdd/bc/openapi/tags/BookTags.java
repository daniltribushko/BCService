package ru.tdd.bc.openapi.tags;

import io.swagger.v3.oas.models.tags.Tag;

/**
 * @author Tribushko Danil
 * @since 09.06.2026
 * Тэги для работы с книгами
 */
public class BookTags {

    private BookTags() {}

    public static final Tag BOOK_TAG = new Tag()
            .name("Book Controller")
            .description("Эндпоинты для работы с книгами");

    public static final Tag GENRE_TAG = new Tag()
            .name("Tag Controller")
            .description("Эндпоинты для работы с жанрами книг");

    public static final Tag PUBLISHER_TAG = new Tag()
            .name("Publisher Tag")
            .description("Эндпоинты для работы с издателями книг");
}
