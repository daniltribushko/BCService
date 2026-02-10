package ru.tdd.user.application.services.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tdd.user.application.models.dto.JwtTokenDTO;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.application.services.JwtTokenService;
import ru.tdd.user.application.utils.DateUtils;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tribushko Danil
 * @since 31.01.2026
 */
@Service
public class JwtTokenServiceImp implements JwtTokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public JwtTokenDTO generate(SystemUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("system", true);
        claims.put("roles", user.getRoles().stream().map(Role::name).toArray());

        Date issueAt = new Date();
        Date expiration = DateUtils.plusTime(issueAt, DateUtils.DAY);

        String token = Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(issueAt)
                .expiration(expiration)
                .signWith(getSecret())
                .compact();

        return new JwtTokenDTO(
                token,
                expiration.getTime()
        );
    }

    @Override
    public JwtTokenDTO generate(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("system", true);
        claims.put("roles", user.getRoles().stream().map(Role::name).toArray());
        claims.put("email", user.getEmail());
        claims.put("chatId", user.getChatId());


        Date issueAt = new Date();
        Date expiration = DateUtils.plusTime(issueAt, DateUtils.DAY);

        String token = Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(issueAt)
                .expiration(expiration)
                .signWith(getSecret())
                .compact();

        return new JwtTokenDTO(
                token,
                expiration.getTime()
        );
    }

    @Override
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(getSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean validate(String toke) {
        Claims claims = parse(toke);
        return DateUtils.isFuture(claims.getExpiration());
    }

    public SecretKey getSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
