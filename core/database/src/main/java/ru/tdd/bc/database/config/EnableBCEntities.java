package ru.tdd.bc.database.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.lang.annotation.*;

/**
 * @author Tribushko Danil
 * @since 05.06.202
 * Аннотация для добавления сущностей в контекст
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EntityScan("ru.tdd.bc.database.entity")
public @interface EnableBCEntities {
}
