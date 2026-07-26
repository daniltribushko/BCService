package ru.tdd.bc.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.bc.database.entity.NameEntity;
import ru.tdd.bc.utils.TextUtils;

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
                TextUtils.isEmpty(name) ?
                        cb.conjunction() :
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + name.toLowerCase() + "%"
                        );
    }

    /**
     * Проверка наличия объекта с указанным именем
     */
    static <T extends NameEntity> Specification<T> byNameEqual(String name) {
        return (root, cr, cb) ->
                cb.equal(
                        cb.lower(root.get("name")),
                        name.toLowerCase()
                );
    }
}
