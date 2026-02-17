package ru.tdd.user.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.tdd.user.database.entities.user.SystemUser;

public class SecurityContextUtils {

    public static void setUserInContext(SystemUser user) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        null
                )
        );
        SecurityContextHolder.setContext(context);
    }
}
