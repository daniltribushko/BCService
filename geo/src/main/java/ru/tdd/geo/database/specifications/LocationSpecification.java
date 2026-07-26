package ru.tdd.geo.database.specifications;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.tdd.bc.database.criteria.CriteriaHelper;
import ru.tdd.geo.database.entities.Location;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 13.01.2026
 * Набор спецификаций для работы с локациями
 */
public interface LocationSpecification {

    /**
     * Поиск по названиею и идентификатору города
     */
    static Specification<Location> byNameAndCityIdEqual(String name, UUID id) {
        return (root, cr, cb) -> {
            var helper = new CriteriaHelper<>(root, cr, cb);
            return cb.and(
                    helper.lowerEqual("name", name)
                            .equal(root.join("city").get("id"), id)
                            .build()
            );
        };
    }

    /**
     * Поиск по названию локации и названию города
     */
    static Specification<Location> byNameAndCityNameFulltextSearch(
            String name,
            String cityName,
            String regionName,
            String countryName
    ) {
        return (root, cr, cb) -> {
            var helper = new CriteriaHelper<>(root, cr, cb);
            return cb.and(
                    helper.like("name", name)
                            .like(root.join("city").get("name"), cityName)
                            .like(root.join("city").join("region", JoinType.LEFT).get("name"), regionName)
                            .like(root.join("city").join("country").get("name"), countryName)
                            .build()

            );
        };
    }
}
