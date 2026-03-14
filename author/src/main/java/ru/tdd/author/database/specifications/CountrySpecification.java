package ru.tdd.author.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.author.database.entitites.Country;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Набор спецификаций для работы со странами
 */
public interface CountrySpecification {

    static Specification<Country> byNameLike(String name) {
        return (root, cr, cb) ->
                new CriteriaHelper<>(root, cr, cb).like("name", name).buildOne();
    }
}
