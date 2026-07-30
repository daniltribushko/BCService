package ru.tdd.bc.book.controller.rest_controllers;

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.bc.book.application.dto.authors.*;
import ru.tdd.bc.book.controller.configs.OpenApiConfig;
import ru.tdd.bc.book.controller.open_api.annotations.AuthorFioQueryParameter;
import ru.tdd.bc.book.controller.open_api.annotations.CountryNameQueryParameter;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.openapi.annotations.author.AuthorIdPathParameter;
import ru.tdd.bc.openapi.annotations.parameters.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribusho Danil
 * @since 21.02.2026
 * Контроллер для работы с авторами
 */
@RequestMapping("/books/authors")
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
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
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
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @PutMapping("/{authorId}")
    ResponseEntity<AuthorDTO> update(@PathVariable("authorId") @AuthorIdPathParameter UUID authorId, @RequestBody UpdateAuthorDTO dto);

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
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_USER")
    @GetMapping("/{authorId}")
    ResponseEntity<AuthorDetailsDTO> getById(@PathVariable(name = "authorId") @AuthorIdPathParameter UUID authorId);

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
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{authorId}")
    ResponseEntity<?> delete(@PathVariable(name = "authorId") @AuthorIdPathParameter UUID authorId);

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
    @Secured("ROLE_USER")
    @GetMapping
    ResponseEntity<AuthorListDTO> getAll(
            @AuthorFioQueryParameter
            @RequestParam(name = "fio", required = false)
            String fio,
            @CountryNameQueryParameter
            @RequestParam(name = "country_name", required = false)
            String countryName,
            @PageQueryParameter
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @PerPageQueryParameter
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
    @Secured("ROLE_ADMIN")
    @GetMapping("/details")
    ResponseEntity<AuthorDetailsListDTO> getAllDetails(
            @AuthorFioQueryParameter
            @RequestParam(name = "fio", required = false)
            String fio,
            @CountryNameQueryParameter
            @RequestParam(name = "country_name", required = false)
            String countryName,
            @StartCreationTimeQueryParameter
            @RequestParam(name = "creation_time_start", required = false)
            LocalDateTime creationTimeStart,
            @EndCreationTimeQueryParameter
            @RequestParam(name = "creation_time_end", required = false)
            LocalDateTime creationTimeEnd,
            @StartUpdateTimeQueryParameter
            @RequestParam(name = "update_time_start", required = false)
            LocalDateTime updateTimeStart,
            @EndUpdateTimeQueryParameter
            @RequestParam(name = "update_time_end", required = false)
            LocalDateTime updateTimeEnd,
            @PageQueryParameter
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @PerPageQueryParameter
            @RequestParam(name = "per_page", required = false, defaultValue = "100")
            int perPage
    );
}
