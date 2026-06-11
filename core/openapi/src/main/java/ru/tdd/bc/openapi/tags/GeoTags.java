package ru.tdd.bc.openapi.tags;

import io.swagger.v3.oas.models.tags.Tag;

/**
 * @author Tribushko Danil
 * @since 09.06.20206
 * Тэги для работы с локациями
 */
public class GeoTags {

    private GeoTags() {}

    public static final Tag COUNTRY_TAG = new Tag()
            .name("Country Controller")
            .description("Эндпоинты для работы со странами");
}
