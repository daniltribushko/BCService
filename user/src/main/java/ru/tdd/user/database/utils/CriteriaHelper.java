package ru.tdd.user.database.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.tdd.user.application.utils.TextUtils;
import ru.tdd.user.database.entities.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tribushko Danil
 * @since 14.02.2026
 * Оболочка для работы с Criteria Api
 */
public class CriteriaHelper<T extends BaseEntity> {

    private Root<T> root;

    private jakarta.persistence.criteria.CriteriaBuilder cb;

    private CriteriaQuery<?> cr;

    private List<Predicate> predicates;

    public CriteriaHelper(Root<T> root, CriteriaQuery<?> cr, CriteriaBuilder cb) {
        this.root = root;
        this.cb = cb;
        this.cr = cr;
        this.predicates = new ArrayList<>();
    }

    public CriteriaHelper<T> like(String field, String text) {
        if (!TextUtils.isEmpty(text))
            predicates.add(cb.like(cb.lower(root.get(field)), "%" + text.toLowerCase() + "%"));
        return this;
    }

    public CriteriaHelper<T> equal(String field, String text) {
        if (!TextUtils.isEmpty(text))
            predicates.add(cb.equal(cb.lower(root.get(field)), text.toLowerCase()));
        return this;
    }

    public CriteriaHelper<T> enumsEqual(String field, List<? extends Enum> enums) {
        if (enums != null && !enums.isEmpty())
            predicates.add(
                    cb.or(
                            enums.stream()
                                    .map(r -> cb.like(root.get(field), "%" + r.toString() + "%"))
                                    .toArray(Predicate[]::new)
                    )
            );
        return this;
    }

    public CriteriaHelper<T> inDateRange(String field, LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null)
            predicates.add(cb.between(root.get(field), start, end));
        else if (start != null)
            predicates.add(cb.greaterThanOrEqualTo(root.get(field), start));
        else if (end != null)
            predicates.add(cb.lessThanOrEqualTo(root.get(field), end));

        return this;
    }

    public Predicate[] build() {
        return predicates.toArray(new Predicate[0]);
    }
}
