package ru.tdd.geo.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.geo.application.utils.TextUtils;
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
        return (root, cb, cr) -> {
            if (name == null && id == null) {
                if (id == null)
                    throw new NullPointerException("Необходимо указать идентификатор страны");
                else
                    throw new NullPointerException("Необходимо указать название региона");
            }

            return cr.and(
                    cr.equal(
                            cr.lower(root.get("name")),
                            name.toLowerCase()
                    ),
                    cr.equal(
                            root.join("country")
                                    .get("id"),
                            id
                    )
            );
        };
    }

    /**
     * Поиск по названию региона и названию страны региона с полнотекстовым поиском
     */
    static Specification<Region> byNameAndCountryNameFullTextSearch(String name, String countryName) {
        return (root, cr, cb) ->
                cb.and(
                        TextUtils.isEmptyWithNull(name) ?
                                cb.conjunction() :
                                cb.like(
                                        cb.lower(
                                                root.get("name")
                                        ),
                                        "%" + name.toLowerCase() + "%"
                                ),
                        TextUtils.isEmptyWithNull(countryName) ? cb.conjunction() :
                                cb.like(
                                        cb.lower(
                                                root.join("country").get("name")
                                        ),
                                        "%" + countryName.toLowerCase() + "%"
                                )
                );
    }
}
