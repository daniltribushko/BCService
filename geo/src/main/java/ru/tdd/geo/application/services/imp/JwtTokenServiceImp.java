package ru.tdd.geo.application.services.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tdd.geo.application.models.dto.UserDTO;
import ru.tdd.geo.application.models.enums.Role;
import ru.tdd.geo.application.services.JwtTokenService;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 */
@Service
public class JwtTokenServiceImp implements JwtTokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean validateToken(String token) {
        return parseToken(token).getExpiration().after(new Date());
    }

    @Override
    public UserDTO getUser(String token) {
        Claims claims = parseToken(token);
        List<String> stringRoles = claims.get("roles", List.class);
        return new UserDTO(
                UUID.fromString(claims.get("id", String.class)),
                claims.get("chatId", Long.class),
                claims.getSubject(),
                stringRoles.stream().map(Role::valueOf).toList()
        );
    }

    @Override
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
