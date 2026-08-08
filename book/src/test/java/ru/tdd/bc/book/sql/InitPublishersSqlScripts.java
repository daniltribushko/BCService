package ru.tdd.bc.book.sql;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.*;

/**
 * @author Tribushko Danil
 * @since 01.08.2026
 * Аннотация для инициализации издателей при помощи sql скриптов
 */
@SqlGroup(
        value = {
                @Sql(
                        scripts = {
                                "/scripts/insert_countries.sql",
                                "/scripts/insert_publishers.sql"
                        },
                        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
                ),
                @Sql(
                        scripts = {
                                "/scripts/clean_publishers.sql",
                                "/scripts/clean_countries.sql"
                        },
                        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
                )
        }
)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InitPublishersSqlScripts {
}
