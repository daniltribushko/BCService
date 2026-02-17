package ru.tdd.user.controller.rest_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.user.application.models.dto.*;
import ru.tdd.user.application.services.UserService;
import ru.tdd.user.controller.config.OpenApiConfig;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.02.2026
 * Контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/users")
@Tag(name = OpenApiConfig.USER_CONTROLLER)
@SecurityRequirement(name = "jwtAuth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Update", description = "Обновление пользователя")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Пользователь успешно обновлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Пользователь не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Пользователь уже создан",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные не валидны",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @PathVariable
            UUID id,
            @RequestBody
            UpdateUserDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, dto));
    }

    @Operation(summary = "Delete", description = "Удаление пользователя")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "Пользователь успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Пользователь пытается удалить другого пользователя",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Пользователь не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get By Id", description = "Получение пользователя по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Пользователь успешно получен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Пользователь не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getById(id));
    }

    @Operation(summary = "Get All", description = "Получение списка пользователей")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Пользователи получены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserListDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Пользователь не является администратором",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<UserListDTO> getAll(
            @RequestBody GetUserListParametersDTO dto,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "100")
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.getAll(
                        dto,
                        page,
                        perPage
                )
        );
    }
}
