package ru.tdd.user.application.services;

import io.jsonwebtoken.Claims;
import ru.tdd.user.application.models.dto.JwtTokenDTO;
import ru.tdd.user.database.entities.user.SystemUser;

/**
 * @author Tribushko Danil
 * @since 29.01.2026
 */
public interface JwtTokenService {

    JwtTokenDTO generate(SystemUser user);

    Claims parse(String token);

    boolean validate(String toke);
}
