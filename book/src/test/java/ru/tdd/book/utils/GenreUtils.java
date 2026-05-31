package ru.tdd.book.utils;

import ru.tdd.book.database.entities.Genre;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 06.05.2026
 * Набор утилит для работы с жанрами в тестах
 */
public class GenreUtils {

    private GenreUtils() {}

    public static final UUID GENRE_ID1 = UUID.fromString("de5d943e-54ea-4b0c-a0fa-98fd137f7caf");

    public static final UUID GENRE_ID2 = UUID.fromString("138553fc-bfa4-4b70-8fd8-bc249715d3f0");

    public static final Genre GENRE2 = Genre.builder()
            .id(GENRE_ID2)
            .build();

    public static final UUID GENRE_ID3 = UUID.fromString("710c5e2c-734e-4edb-9d0c-a28e7414e4e3");

    public static final UUID GENRE_ID4 = UUID.fromString("dc0a3c74-807e-44a4-93f0-204ea453ea64");

    public static final UUID GENRE_ID5 = UUID.fromString("63afb5fd-5f12-4a0b-a008-ba860d997d11");
}
