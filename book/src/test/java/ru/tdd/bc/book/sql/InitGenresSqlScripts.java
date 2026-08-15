package ru.tdd.bc.book.sql;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.*;

/**
 * @author Tribushko Danil
 * @since 09.08.2026
 * Аннотация для инициализации жанров книг при помощи sql скриптов
 */
@SqlGroup(
        value = {
                @Sql(
                        scripts = "/scripts/insert_genres.sql",
                        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
                ),
                @Sql(
                        scripts = "/scripts/clean_genres.sql",
                        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
                )
        }
)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InitGenresSqlScripts {
}
