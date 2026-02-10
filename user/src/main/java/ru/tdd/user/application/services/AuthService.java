package ru.tdd.user.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.user.application.models.dto.JwtTokenDTO;
import ru.tdd.user.application.models.dto.SignIn;
import ru.tdd.user.application.models.dto.SignUp;

/**
 * @author Tribushko Danil
 * @since 03.02.2026
 * Сервис для авторизации и регистрации пользователей
 */
public interface AuthService {

    @Transactional
    JwtTokenDTO signUp(SignUp signUp);

    @Transactional
    JwtTokenDTO signIn(SignIn signIn);
}
