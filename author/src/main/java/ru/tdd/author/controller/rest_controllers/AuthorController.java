package ru.tdd.author.controller.rest_controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tdd.author.application.dto.ExceptionDTO;
import ru.tdd.author.application.dto.authors.*;
import ru.tdd.author.controller.confing.OpenApiConfig;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribusho Danil
 * @since 21.02.2026
 * Контроллер для работы с авторами
 */
@RequestMapping("/authors")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.AUTHOR_CONTROLLER)
public interface AuthorController {

    @Operation(summary = "Create", description = "Создание автора, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Автор успешно создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    ResponseEntity<AuthorDTO> create(@Valid @RequestBody CreateAuthorDTO dto);

    @Operation(summary = "Update", description = "Обновление автора, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Автор обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthorDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Автор не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    ResponseEntity<AuthorDTO> update(@PathVariable UUID id, @RequestBody UpdateAuthorDTO dto);

    @Operation(summary = "Get By Id", description = "Получение автора с детальной информации по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Автор получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthorDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Автор не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<AuthorDetailsDTO> getById(@PathVariable UUID id);

    @Operation(summary = "Delete", description = "Удаление пользователя, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "Автор успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Пользователь не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable UUID id);

    @Operation(summary = "Get All", description = "Получение списка авторов")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Авторы получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthorListDTO.class)
                            )
                    )
            }
    )
    @GetMapping
    ResponseEntity<AuthorListDTO> getAll(
            @RequestParam(name = "fio", required = false)
            String fio,
            @RequestParam(name = "country_name", required = false)
            String countryName,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "100")
            int perPage
    );

    @Operation(summary = "Get All Details", description = "Получение списка авторов с подробной информацией")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Авторы успешно получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthorDetailsListDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/details")
    ResponseEntity<AuthorDetailsListDTO> getAllDetails(
            @RequestParam(name = "fio", required = false)
            String fio,
            @RequestParam(name = "country_name", required = false)
            String countryName,
            @RequestParam(name = "creation_time_start", required = false)
            LocalDateTime creationTimeStart,
            @RequestParam(name = "creation_time_end", required = false)
            LocalDateTime creationTimeEnd,
            @RequestParam(name = "update_time_start", required = false)
            LocalDateTime updateTimeStart,
            @RequestParam(name = "update_time_end", required = false)
            LocalDateTime updateTimeEnd,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "100")
            int perPage
    );
}
