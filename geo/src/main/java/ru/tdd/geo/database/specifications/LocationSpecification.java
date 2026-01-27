package ru.tdd.geo.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.geo.application.utils.TextUtils;
import ru.tdd.geo.database.entities.Location;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 13.01.2026
 * Набор спецификаций для работы с локациями
 */
public interface LocationSpecification {

    /** Поиск по названиею и идентификатору города */
    static Specification<Location> byNameAndCityIdEqual(String name, UUID id) {
        return (root, cr, cb) ->
                cb.and(
                        cb.equal(cb.lower(root.get("name")), name.toLowerCase()),
                        cb.equal(root.join("city").get("id"), id)
                );
    }

    /** Поиск по названию локации и названию города */
    static Specification<Location> byNameAndCityNameFulltextSearch(String name, String cityName) {
        return (root, cr, cb) ->
                cb.and(
                        TextUtils.isEmptyWithNull(name) ?
                                cb.conjunction() :
                                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"),
                        TextUtils.isEmptyWithNull(cityName) ?
                                cb.conjunction() :
                                cb.like(
                                        cb.lower(root.join("city").get("name")),
                                        "%" + cityName.toLowerCase() + "%"
                                )
                );
    }
}
