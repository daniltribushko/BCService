package ru.tdd.geo.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.core.application.utils.TextUtils;
import ru.tdd.geo.database.entities.City;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * Набор спецификаций для городов
 */
public interface CitySpecification {

    /** Поиск по название, идентификатору региона, идентификатору страны */
    static Specification<City> byNameRegionCityEqual(String name, UUID regionId, UUID countryId) {
        return (root, cr, cb) ->
                cb.and(
                        cb.equal(cb.lower(root.get("name")), name.toLowerCase()),
                        regionId == null ? cb.isNull(root.get("region")) : cb.equal(root.get("region").get("id"), regionId),
                        cb.equal(root.get("country").get("id"), countryId)
                );

    }

    /** Поиск по названию города, названию региона, названию страны */
    static Specification<City> byNameRegionCityFullTextSearch(String name, String regionName, String countryName) {
        return (root, cr, cb) ->
                cb.and(
                        TextUtils.isEmpty(name) ?
                                cb.conjunction() :
                                cb.like(
                                        cb.lower(root.get("name")),
                                        "%" + name.toLowerCase() + "%"
                                ),
                        TextUtils.isEmpty(regionName) ?
                                cb.conjunction() :
                                cb.like(
                                        cb.lower(root.join("region").get("name")),
                                        "%" + regionName.toLowerCase() + "%"
                                ),
                        TextUtils.isEmpty(countryName) ?
                                cb.conjunction() :
                                cb.like(
                                        cb.lower(root.join("country").get("name")),
                                        "%" + countryName.toLowerCase() + "%"
                                )
                );
    }
}
