package ru.tdd.geo.controller.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tdd.geo.application.models.dto.UserDTO;
import ru.tdd.geo.application.models.exceptions.ApiException;
import ru.tdd.geo.application.services.JwtTokenService;
import ru.tdd.geo.application.utils.TextUtils;

import java.io.IOException;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * Фильтер для jwt токена
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_NAME = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtFilter(
            JwtTokenService jwtTokenService
    ) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_NAME);

        if (!TextUtils.isEmptyWithNull(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            if (jwtTokenService.validateToken(token)) {
                String username = jwtTokenService.parseToken(token).getSubject();
                if (!TextUtils.isEmptyWithNull(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDTO user = jwtTokenService.getUser(token);
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            } else
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Токен не валидный");
        }

        filterChain.doFilter(request, response);
    }
}
