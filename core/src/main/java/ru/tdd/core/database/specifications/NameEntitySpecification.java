package ru.tdd.core.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.core.application.utils.TextUtils;
import ru.tdd.core.database.criteria.CriteriaHelper;
import ru.tdd.core.database.entities.NameEntity;

/**
 * @author Tribushko Danil
 * @since 03.05.2026
 * Набор спецификаций для 
 */
public interface NameEntitySpecification {

    static <T extends NameEntity> Specification<T> byName(String name) {
        return (root, query, builder) -> {
            if (TextUtils.isEmpty(name))
                return builder.conjunction();
            else
                return new CriteriaHelper<>(root, query, builder)
                        .like("name", name)
                        .buildOne();
        };
    }
}
