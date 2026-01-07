package ru.tdd.geo.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.geo.application.utils.TextUtils;
import ru.tdd.geo.database.entities.BaseNameEntity;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Набор спецификаций для сущностей с полем "name"
 */
public interface NameSpecification {

    /**
     * Поиск объекта по полному совподению имени
     */
    static <T extends BaseNameEntity> Specification<T> byNameWithFullTextSearch(String name) {
        return (root, cr, cb) ->
                TextUtils.isEmptyWithNull(name) ?
                        cb.conjunction() :
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + name.toLowerCase() + "%"
                        );
    }

    /**
     * Проверка наличия объекта с указанным именем
     */
    static <T extends BaseNameEntity> Specification<T> byNameEqual(String name) {
        return (root, cr, cb) ->
                cb.equal(
                        cb.lower(root.get("name")),
                        name.toLowerCase()
                );
    }
}
