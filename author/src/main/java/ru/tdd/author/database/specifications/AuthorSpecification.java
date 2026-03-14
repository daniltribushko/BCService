package ru.tdd.author.database.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ru.tdd.author.application.utils.TextUtils;
import ru.tdd.author.database.entitites.Author;
import ru.tdd.author.database.entitites.Country;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Набор спецификаций авторов
 */
public interface AuthorSpecification {

    private static CriteriaHelper<Author> getPredicateByFio(CriteriaHelper<Author> ch, String text) {
        return ch.like("lastName", text)
                .like("middleName", text)
                .like("firstName", text);
    }

    private static CriteriaHelper<Author> getFioPredicate(CriteriaHelper<Author> helper, String fio) {
        if (!TextUtils.isEmpty(fio))
            Arrays.stream(fio.split("\\s+"))
                    .forEach(text ->
                            helper.or(getPredicateByFio(helper.getEmptyHelper(), text).build())
                    );

        return helper;
    }

    private static Predicate[] getCountryNamePredicate(String countryName, Root<Author> root, CriteriaQuery<?> cr, CriteriaBuilder cb) {
        Root<Country> countryRoot = cr.from(Country.class);

        CriteriaHelper<Country> countryHelper = new CriteriaHelper<>(countryRoot, cr, cb);

        Predicate[] predicates = new Predicate[2];

        predicates[0] = cb.equal(
                root.get("country"),
                countryRoot.get("id")
        );

        predicates[1] = countryHelper.like("name", countryName).buildOne();

        return predicates;
    }

    static Specification<Author> byFioAndCountryNameDate(
            String fio,
            String countryName
    ) {
        return (root, cr, cb) -> {
            CriteriaHelper<Author> helper = new CriteriaHelper<>(root, cr, cb);

            Predicate authorPredicate = cb.and(getFioPredicate(helper, fio)
                    .build()
            );

            assert cr != null;
            cr.distinct(true);

            if (TextUtils.isEmpty(countryName))
                return authorPredicate;
            else {
                Predicate[] countryPredicates = getCountryNamePredicate(
                        countryName,
                        root,
                        cr,
                        cb
                );

                return cb.and(
                        authorPredicate,
                        countryPredicates[0],
                        countryPredicates[1]
                );
            }
        };
    }

    static Specification<Author> byFioAndCountryNameAndVersionsDate(
            String fio,
            String countryName,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd
    ) {
        return (root, cr, cb) -> {
            CriteriaHelper<Author> helper = new CriteriaHelper<>(root, cr, cb);

            Predicate authorPredicate = cb.and(getFioPredicate(helper, fio)
                    .inDateRange("creationTime", creationTimeStart, creationTimeEnd)
                    .inDateRange("updateTime", updateTimeStart, updateTimeEnd)
                    .build()
            );

            assert cr != null;
            cr.distinct(true);

            if (TextUtils.isEmpty(countryName))
                return authorPredicate;
            else {
                Predicate[] countryPredicates = getCountryNamePredicate(
                        countryName,
                        root,
                        cr,
                        cb
                );

                return cb.and(
                        authorPredicate,
                        countryPredicates[0],
                        countryPredicates[1]
                );
            }
        };
    }
}
