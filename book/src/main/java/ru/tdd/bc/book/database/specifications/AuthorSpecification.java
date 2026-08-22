package ru.tdd.bc.book.database.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ru.tdd.bc.book.database.entities.Author;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.database.criteria.CriteriaHelper;
import ru.tdd.bc.utils.TextUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

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
                root.get("country").get("id"),
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

    static Specification<Author> byFioAndCountryNameAndBirthdayAndVersionsDate(
            String fio,
            String countryName,
            LocalDate startBirthday,
            LocalDate endBirthday,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd
    ) {
        return (root, cr, cb) -> {
            CriteriaHelper<Author> helper = new CriteriaHelper<>(root, cr, cb);

            Predicate authorPredicate = cb.and(getFioPredicate(helper, fio)
                    .inDateRange("birthday", startBirthday, endBirthday)
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

    static Specification<Author> byLastNameFirstNameBirthdayCountryId(
            String lastName,
            String firstName,
            LocalDate birthday,
            UUID countryId
    ) {
        return (root, cr, cb) -> {
            var helper = new CriteriaHelper<>(root, cr, cb)
                    .lowerEqual("lastName", lastName)
                    .lowerEqual("firstName", firstName)
                    .isNull("id")
                    .equal(root.join("country").get("id"), countryId);

            if (birthday == null)
                helper.isNull("birthday");
            else
                helper.equal("birthday", birthday);

            return cb.and(helper.build());
        };
    }
}

