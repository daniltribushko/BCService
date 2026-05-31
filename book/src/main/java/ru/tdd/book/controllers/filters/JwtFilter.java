package ru.tdd.book.controllers.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tdd.core.application.exceptions.AuthorizationException;
import ru.tdd.core.application.services.JwtService;
import ru.tdd.core.application.utils.TextUtils;
import ru.tdd.core.controller.dto.users.UserDto;

import java.io.IOException;

/**
 * @author Tribushko Danil
 * @since 10.05.2026
 * Фильтр для jwt токенов
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer";

    private final JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    public JwtFilter(
            JwtService jwtService
    ) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!TextUtils.isEmpty(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length() + 1);

            if (jwtService.validateToken(token, secretKey)) {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDto user = jwtService.parseToken(token, secretKey);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

                    var emptyContext = SecurityContextHolder.createEmptyContext();
                    emptyContext.setAuthentication(auth);
                    SecurityContextHolder.setContext(emptyContext);
                }
            } else
                throw new AuthorizationException("Токен не валидный");
        }

        filterChain.doFilter(request, response);
    }
}
