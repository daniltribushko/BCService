package ru.tdd.geo.application.services.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tdd.geo.application.services.JwtTokenService;

import javax.crypto.SecretKey;
import java.util.Date;

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
