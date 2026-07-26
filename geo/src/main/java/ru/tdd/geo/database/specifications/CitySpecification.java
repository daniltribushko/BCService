package ru.tdd.geo.database.specifications;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.tdd.bc.database.criteria.CriteriaHelper;
import ru.tdd.geo.database.entities.City;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * Набор спецификаций для городов
 */
public interface CitySpecification {

    /**
     * Поиск по название, идентификатору региона, идентификатору страны
     */
    static Specification<City> byNameRegionCityEqual(String name, UUID regionId, UUID countryId) {
        return (root, cr, cb) ->
                cb.and(
                        new CriteriaHelper<>(root, cr, cb)
                                .equal("name", name)
                                .predicate(
                                        regionId == null ?
                                                cb.isNull(root.get("region")) :
                                                cb.equal(root.get("region").get("id"), regionId)
                                )
                                .equal(root.get("country").get("id"), countryId)
                                .build()
                );

    }

    /**
     * Поиск по названию города, названию региона, названию страны
     */
    static Specification<City> byNameRegionCityFullTextSearch(String name, String regionName, String countryName) {
        return (root, cr, cb) ->
                cb.and(
                        new CriteriaHelper<>(root, cr, cb)
                                .like("name", name)
                                .like(cb.lower(root.join("region", JoinType.LEFT).get("name")), regionName)
                                .like(cb.lower(root.join("country").get("name")), countryName)
                                .build()
                );
    }
}
