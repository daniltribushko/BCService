package ru.tdd.user.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tdd.user.application.models.dto.JwtTokenDTO;
import ru.tdd.user.application.models.dto.SignIn;
import ru.tdd.user.application.models.dto.SignUp;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.application.models.exceptions.user.UserByChatIdAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByEmailAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByUsernameAlreadyExistsException;
import ru.tdd.user.application.services.AuthService;
import ru.tdd.user.application.services.JwtTokenService;
import ru.tdd.user.application.utils.TextUtils;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tribushko Danil
 * @since 03.02.2026
 */
@Service
public class AuthServiceImp implements AuthService {

    private final SystemUserRepository systemUserRepository;

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthServiceImp(
            SystemUserRepository systemUserRepository,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UserDetailsService userDetailsService
    ) {
        this.systemUserRepository = systemUserRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public JwtTokenDTO signUp(SignUp signUp) {
        String username = signUp.getUsername();
        String password = signUp.getPassword();
        String email = signUp.getEmail();
        Long chatId = signUp.getChatId();

        if (systemUserRepository.existsByUsername(username))
            throw new UserByUsernameAlreadyExistsException();

        if (!TextUtils.isEmpty(email) && appUserRepository.existsByEmail(email))
            throw new UserByEmailAlreadyExistsException();

        if (chatId != null && appUserRepository.existsByChatId(chatId))
            throw new UserByChatIdAlreadyExistsException();

        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);

        AppUser user = AppUser.appUserBuilder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .chatId(chatId)
                .roles(roles)
                .build();

        appUserRepository.save(user);

        return jwtTokenService.generate(user);
    }

    @Override
    public JwtTokenDTO signIn(SignIn signIn) {
        String username = signIn.getUsername();

        UserDetails user = userDetailsService.loadUserByUsername(username);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, signIn.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }

        JwtTokenDTO result ;

        if (user instanceof AppUser)
            result = jwtTokenService.generate((AppUser) user);
        else
            result = jwtTokenService.generate((SystemUser) user);

        return result;
    }
}
