package ru.tdd.bc.book.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.bc.book.database.entities.Publisher;
import ru.tdd.bc.database.criteria.CriteriaHelper;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @see 01.08.2026
 * Набор спецификаций для издателя
 */
public interface PublisherSpecification {

    static Specification<Publisher> byNameCountryNameLike(
            String name,
            String countryName,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd
    ) {
        return (root, cr, cb) -> cb.and(
                new CriteriaHelper<>(root, cr, cb)
                        .like("name", name)
                        .like(root.join("country").get("name"), countryName)
                        .inDateRange("creationTime", creationTimeStart, creationTimeEnd)
                        .inDateRange("updateTime", updateTimeStart, updateTimeEnd)
                        .build()
        );
    }

    static Specification<Publisher> byNameAndCountryEqual(
            String name,
            UUID countryId
    ) {
        return (root, cr, cb) -> cb.and(
                new CriteriaHelper<>(root, cr, cb)
                        .lowerEqual("name", name)
                        .equal(root.join("country").get("id"), countryId)
                        .build()
        );
    }

    static Specification<Publisher> byNameAndCountryEqual(
            String name,
            UUID countryId,
            UUID id
    ) {
        return (root, cr, cb) -> cb.and(
                new CriteriaHelper<>(root, cr, cb)
                        .lowerEqual("name", name)
                        .equal(root.join("country").get("id"), countryId)
                        .notEqual("id", id)
                        .build()
        );
    }
}
