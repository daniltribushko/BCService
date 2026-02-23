package ru.tdd.author.application.services.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tdd.author.application.dto.UserDTO;
import ru.tdd.author.application.enums.Role;
import ru.tdd.author.application.exceptions.ForbiddenException;
import ru.tdd.author.application.services.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 */
@Component
public class JwtServiceImp implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public UserDTO parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (claims.getExpiration().before(new Date()))
            throw new ForbiddenException("Токен не валидный");

        List<String> rolesString = claims.get("roles", List.class);

        return new UserDTO(
                UUID.fromString(claims.get("id", String.class)),
                claims.get("chatId", Long.class),
                claims.getSubject(),
                rolesString.stream().map(Role::valueOf)
                        .toList()

        );
    }

    private SecretKey getSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
