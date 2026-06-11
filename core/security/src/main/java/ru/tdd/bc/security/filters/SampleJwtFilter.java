package ru.tdd.bc.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tdd.bc.http.AuthorizationException;
import ru.tdd.bc.security.dto.UserDto;
import ru.tdd.bc.security.jwt.JwtService;
import ru.tdd.bc.utils.TextUtils;

import java.io.IOException;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Базовый фильтер для jwt токенов
 */
public class SampleJwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer";

    private final JwtService jwtService;

    private final String secretKey;

    public SampleJwtFilter(JwtService jwtService, String secretKey) {
        this.jwtService = jwtService;
        this.secretKey = secretKey;
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
