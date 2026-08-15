package ru.tdd.bc.book.controller.rest_controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.bc.book.controller.open_api.annotations.GenreIdPathParameter;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionariesListDataDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.openapi.annotations.parameters.PageQueryParameter;
import ru.tdd.bc.openapi.annotations.parameters.PerPageQueryParameter;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.08.2026
 * Контроллер для работы с жанрами
 */
@RequestMapping("/books/genres")
@SecurityRequirement(name = "jwtAuth")
public interface GenreController {

    @Operation(summary = "Create", description = "Создание жанра, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Жанр успешно создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DictionaryDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Жанр уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    ResponseEntity<DictionaryDto> create(@Valid @RequestBody CreateDictionaryDto dto);

    @Operation(summary = "Update", description = "Обновление жанра, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Жанр успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DictionaryDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Жанр не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Жанр уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @PutMapping("/{genreId}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<DictionaryDto> update(
            @PathVariable
            @GenreIdPathParameter
            UUID genreId,
            @Valid
            @RequestBody
            UpdateDictionaryDto dto
    );

    @Operation(summary = "Удаление жанра")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Жанр удален"),
                    @ApiResponse(
                            responseCode = "404", description = "Жанр не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{genreId}")
    ResponseEntity<?> delete(@PathVariable @GenreIdPathParameter UUID genreId);

    @Operation(summary = "Получение жанра по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Жанр успешно получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DictionaryDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Жанр не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_USER")
    @GetMapping("/{genreId}")
    ResponseEntity<DictionaryDto> getById(@PathVariable @GenreIdPathParameter UUID genreId);

    @Operation(summary = "Get All", description = "Получение списка жанров с фильтрами")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Жанры успешно получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DictionariesListDataDto.class)
                            )
                    )
            }
    )
    @GetMapping
    @Secured("ROLE_USER")
    ResponseEntity<DictionariesListDataDto> getAll(
            @RequestParam(name = "name", required = false)
            @Parameter(name = "name", description = "Название жанра", in = ParameterIn.QUERY)
            String name,
            @PageQueryParameter
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @PerPageQueryParameter
            @RequestParam(name = "per_page", required = false, defaultValue = "10")
            int perPage
    );
}
