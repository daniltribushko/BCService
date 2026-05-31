package ru.tdd.book.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.book.database.entities.Genre;
import ru.tdd.core.application.utils.TextUtils;
import ru.tdd.core.database.criteria.CriteriaHelper;

/**
 * @author Tribushko Danil
 * @since 02.04.2026
 * Спецификации для работы с жанрами
 */
public interface GenreSpecification {

    static Specification<Genre> byName(String name) {
        return (root, query, builder) -> {
            if (TextUtils.isEmpty(name))
                return builder.conjunction();
            else
                return
                        new CriteriaHelper<>(root, query, builder)
                                .like("name", name).buildOne();
        };
    }
}
