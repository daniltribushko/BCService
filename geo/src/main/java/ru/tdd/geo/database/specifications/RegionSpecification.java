package ru.tdd.geo.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.bc.database.criteria.CriteriaHelper;
import ru.tdd.geo.database.entities.Region;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Набор спецификаций регионов
 */
public interface RegionSpecification {

    /**
     * Поиск по названию и идентификатору страны
     */
    static Specification<Region> byNameAndCountryIdEqual(String name, UUID id) {
        return (root, cb, cr) -> cr.and(
                new CriteriaHelper<>(root, cb, cr)
                        .lowerEqual("name", name)
                        .equal(root.join("country").get("id"), id)
                        .build()
        );
    }

    /**
     * Поиск по названию региона и названию страны региона с полнотекстовым поиском
     */
    static Specification<Region> byNameAndCountryNameFullTextSearch(String name, String countryName) {
        return (root, cr, cb) ->
                cb.and(
                        new CriteriaHelper<>(root, cr, cb)
                                .like("name", name)
                                .like(root.join("country").get("name"), countryName)
                                .build()
                );
    }
}
