package ru.tdd.bc.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.bc.database.criteria.CriteriaHelper;
import ru.tdd.bc.dictionaries.entities.NameEntity;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Набор спецификаций для сущностей с полем "name"
 */
public interface NameSpecification {

    /**
     * Поиск объекта по полному совподению имени
     */
    static <T extends NameEntity> Specification<T> byNameWithFullTextSearch(String name) {
        return (root, cr, cb) ->
                new CriteriaHelper<>(root, cr, cb).like("name", name).buildOne();
    }

    /**
     * Проверка наличия объекта с указанным именем
     */
    static <T extends NameEntity> Specification<T> byNameEqual(String name) {
        return (root, cr, cb) ->
                new CriteriaHelper<>(root, cr, cb).lowerEqual("name", name).buildOne();
    }
}
