package ru.tdd.user.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;

/**
 * @author Tribusko Danil
 * @since 01.02.2026
 */
@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final SystemUserRepository systemUserRepository;

    private final AppUserRepository appUserRepository;

    @Autowired
    public UserDetailsServiceImp(
            SystemUserRepository systemUserRepository,
            AppUserRepository appUserRepository
    ) {
        this.systemUserRepository = systemUserRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username)
                .map(UserDetails.class::cast)
                .orElseGet(() ->
                        systemUserRepository.findByUsername(username)
                                .map(UserDetails.class::cast)
                                .orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"))
                );
    }
}
