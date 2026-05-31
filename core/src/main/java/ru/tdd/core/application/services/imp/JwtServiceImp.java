package ru.tdd.core.application.services.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import ru.tdd.core.application.services.JwtService;
import ru.tdd.core.application.utils.DateUtils;
import ru.tdd.core.controller.dto.users.Role;
import ru.tdd.core.controller.dto.users.UserDto;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * @author Tribushko Danil
 * @since 04.05.2026
 */
@Service
public class JwtServiceImp implements JwtService {

    private Claims getClaims(String token, String secretKey) {
        return Jwts.parser()
                .verifyWith(getSecret(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public UserDto parseToken(String token, String secretKey) {
        Claims claims = getClaims(token, secretKey);

        List<String> stringRoles = claims.get("roles", List.class);

        return UserDto.builder()
                .id(UUID.fromString(claims.get("id", String.class)))
                .username(claims.getSubject())
                .roles(stringRoles.stream().map(Role::valueOf).toList())
                .build();
    }

    @Override
    public boolean validateToken(String token, String secretKey) {
        Claims claims = getClaims(token, secretKey);

        return claims.getExpiration()
                .after(new Date());
    }

    @Override
    public SecretKey getSecret(String secretKey) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    @Override
    public String generateToken(UserDto user, String secretKey) {
        var claims = new HashMap<String, Object>();

        claims.put("id", user.getId());
        claims.put("roles", user.getRoles().stream().map(Role::name).toList());

        var issueAt = new Date();
        var expiration = new Date(issueAt.getTime() + DateUtils.DAY);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(issueAt)
                .signWith(getSecret(secretKey))
                .expiration(expiration)
                .compact();
    }


}
