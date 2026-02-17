package ru.tdd.user.database.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.utils.CriteriaHelper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Tribushko Danil
 * @since 10.02.2026
 * Набор спецификация для работы с пользователями приложения
 */
public interface AppUserSpecification {

    static Specification<SystemUser> byParameters(
            String username,
            String email,
            List<Role> roles,
            LocalDateTime creationTimeStart,
            LocalDateTime creationTimeEnd,
            LocalDateTime updateTimeStart,
            LocalDateTime updateTimeEnd,
            LocalDateTime lastDateOnlineStart,
            LocalDateTime lastDateOnlineEnd
    ) {
        return (root, cr, cb) ->
            cb.and(
                    new CriteriaHelper<>(root, cr, cb)
                            .like("username", username)
                            .like("email", email)
                            .enumsEqual("roles", roles)
                            .inDateRange("creationTime", creationTimeStart, creationTimeEnd)
                            .inDateRange("updateTime", updateTimeStart, updateTimeEnd)
                            .inDateRange("lastDateOnline", lastDateOnlineStart, lastDateOnlineEnd)
                            .build()
            );
    }
}
