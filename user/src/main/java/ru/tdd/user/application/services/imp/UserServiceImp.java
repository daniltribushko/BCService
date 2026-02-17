package ru.tdd.user.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.tdd.user.application.models.dto.*;
import ru.tdd.user.application.models.exceptions.user.UserByEmailAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByIdNotFoundException;
import ru.tdd.user.application.models.exceptions.user.UserByUsernameAlreadyExistsException;
import ru.tdd.user.application.security.UserSecurity;
import ru.tdd.user.application.services.UserService;
import ru.tdd.user.application.utils.TextUtils;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;
import ru.tdd.user.database.specifications.AppUserSpecification;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.02.2026
 */
@Service
public class UserServiceImp implements UserService {

    private final AppUserRepository appUserRepository;

    private final SystemUserRepository systemUserRepository;

    @Autowired
    public UserServiceImp(
            AppUserRepository appUserRepository,
            SystemUserRepository systemUserRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.systemUserRepository = systemUserRepository;
    }

    @Override
    public UserDTO update(UUID id, UpdateUserDTO dto) {
        UserSecurity.isCurrentUserOwner(id);

        SystemUser user = systemUserRepository.findById(id).orElseThrow(UserByIdNotFoundException::new);

        String email = dto.getEmail();
        String username = dto.getUsername();
        String password = dto.getPassword();

        if (Objects.requireNonNull(user) instanceof AppUser appUser) {
            if (!TextUtils.isEmpty(email))
                if (appUserRepository.existsByEmail(email))
                    throw new UserByEmailAlreadyExistsException();
                else
                    appUser.setEmail(email);
        }

        if (!TextUtils.isEmpty(username))
            if (systemUserRepository.existsByUsername(username))
                throw new UserByUsernameAlreadyExistsException();
            else
                user.setUsername(username);

        if (!TextUtils.isEmpty(password))
            user.setPassword(password);

        user.setUpdateTime(LocalDateTime.now());

        systemUserRepository.save(user);

        return UserDTO.mapFromEntity(user);
    }

    @Override
    public UserDetailsDTO getById(UUID id) {
        return UserDetailsDTO.mapFromEntity(systemUserRepository.findById(id).orElseThrow(UserByIdNotFoundException::new));
    }

    @Override
    public void delete(UUID id) {
        UserSecurity.isCurrentUserOwner(id);

        SystemUser user = systemUserRepository.findById(id).orElseThrow(UserByIdNotFoundException::new);
        systemUserRepository.delete(user);
    }

    @Override
    public UserListDTO getAll(
            GetUserListParametersDTO dto,
            int page,
            int perPage
    ) {
        return
                new UserListDTO(
                        systemUserRepository.findAll(
                                        AppUserSpecification.byParameters(
                                                dto.getUsername(),
                                                dto.getEmail(),
                                                dto.getRoles(),
                                                dto.getCreationTimeStart(),
                                                dto.getCreationTimeEnd(),
                                                dto.getUpdateTimeStart(),
                                                dto.getUpdateTimeEnd(),
                                                dto.getLastDateOnlineStart(),
                                                dto.getLastDateOnlineEnd()
                                        ),
                                        PageRequest.of(page, perPage)
                                ).stream()
                                .map(UserDetailsDTO::mapFromEntity)
                                .toList()
                );
    }
}
