package ru.tdd.geo.sql;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.*;

/**
 * @author Tribushko Danil
 * @since 12.07.2026
 * Аннотация для инициализации городов при помощи sql скриптов
 */
@SqlGroup(
        value = {
                @Sql(
                        scripts = {
                                "/scripts/insert_countries.sql",
                                "/scripts/insert_regions.sql",
                                "/scripts/insert_cities.sql"
                        },
                        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
                ),
                @Sql(
                        scripts = {
                                "/scripts/clean_cities.sql",
                                "/scripts/clean_regions.sql",
                                "/scripts/clean_countries.sql"
                        },
                        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
                )
        }
)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InitCitiesSqlScrips {
}
