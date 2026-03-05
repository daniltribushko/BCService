package ru.tdd.author.controller.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tdd.author.application.dto.UserDTO;
import ru.tdd.author.application.exceptions.ApiException;
import ru.tdd.author.application.services.JwtService;
import ru.tdd.author.application.utils.TextUtils;

import java.io.IOException;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * Фильтер jwt токена
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer";

    private static final String AUTHORISATION_PREFIX = "Authorization";

    private final JwtService jwtService;

    @Autowired
    public JwtFilter(
            JwtService jwtService
    ) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorisationHeader = request.getHeader(AUTHORISATION_PREFIX);
        if (TextUtils.isNonEmpty(authorisationHeader) && authorisationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorisationHeader.substring(BEARER_PREFIX.length() + 1);
            if (TextUtils.isNonEmpty(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDTO user = jwtService.parse(token);
                SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
                emptyContext.setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        )
                );
                SecurityContextHolder.setContext(emptyContext);
            } else
                throw new ApiException(HttpStatus.FORBIDDEN.value(), "") {};
        } else
            filterChain.doFilter(request, response);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (username) -> {
            throw new UnsupportedOperationException();
        };
    }
}
