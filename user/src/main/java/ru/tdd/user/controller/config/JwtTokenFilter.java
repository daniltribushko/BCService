package ru.tdd.user.controller.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tdd.user.application.models.exceptions.ApiException;
import ru.tdd.user.application.services.JwtTokenService;
import ru.tdd.user.application.utils.TextUtils;

import java.io.IOException;

/**
 * @author Tribushko Danil
 * @since 29.01.2026
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer";

    private final JwtTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    public JwtTokenFilter(
            JwtTokenService jwtTokenService,
            UserDetailsService userDetailsService
    ) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!TextUtils.isEmpty(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length() + 1);

            if (jwtTokenService.validate(token)) {
                String username = jwtTokenService.parse(token).getSubject();
                UserDetails user = userDetailsService.loadUserByUsername(username);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(auth);
                    SecurityContextHolder.setContext(context);
                }
            } else
                throw new ApiException(HttpStatus.UNAUTHORIZED.value(), "Токен не валдный");
        } else
            filterChain.doFilter(request, response);
    }
}
