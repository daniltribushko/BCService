package ru.tdd.core.database.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.tdd.core.application.utils.TextUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tribushko Danil
 * @since 14.03.2026
 * Обертка для работы с Criteria Api
 */
public class CriteriaHelper<T> {

    private Root<T> root;

    private CriteriaBuilder cb;

    private CriteriaQuery<?> cr;

    private List<Predicate> predicates;

    public CriteriaHelper(Root<T> root, CriteriaQuery<?> cr, CriteriaBuilder cb) {
        this.root = root;
        this.cb = cb;
        this.cr = cr;
        this.predicates = new ArrayList<>();
    }

    public CriteriaHelper<T> getEmptyHelper() {
        return new CriteriaHelper<>(root, cr, cb);
    }

    public CriteriaHelper<T> like(String field, String text) {
        if (!TextUtils.isEmpty(text))
            predicates.add(
                    cb.like(
                            cb.lower(root.get(field)),
                            "%" + text.toLowerCase() + "%")
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

    public CriteriaHelper<T> or(Predicate[] _predicates) {
        predicates.add(cb.or(_predicates));
        return this;
    }

    public Predicate[] build() {
        return predicates.toArray(new Predicate[0]);
    }

    public Predicate buildOne() {
        if (predicates.isEmpty())
            return cb.conjunction();
        else
            return predicates.getFirst();
    }
}
