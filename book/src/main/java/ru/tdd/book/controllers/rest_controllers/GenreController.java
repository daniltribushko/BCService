package ru.tdd.book.controllers.rest_controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.GenreListDataDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.core.controller.dto.ExceptionDTO;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Контроллер для работы с жанрами
 */
@RequestMapping("/genre")
public interface GenreController {

    @Operation(summary = "Create", description = "Создание жанра")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Жанр успешно создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GenreDto.class)
                            )

                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Жанр уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Данные не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    GenreDto create(@Valid @RequestBody CreateGenreDto dto);

    @Operation(summary = "Update", description = "Обновление жанра")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Жанр успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GenreDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Жанр по идентификатору не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Жанр уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    GenreDto update(
            @PathVariable
            @Parameter(name = "id", in = ParameterIn.PATH, description = "Идентификатор жанра", required = true)
            UUID id,
            @Valid
            @RequestBody
            UpdateGenreDto dto
    );

    @Operation(summary = "Get By Id", description = "Получение жанра по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Жанр успешно получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GenreDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Жанр по идентификатору не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    GenreDto getById(
            @PathVariable
            @Parameter(name = "id", description = "Идентификатор жанра", required = true, in = ParameterIn.PATH)
            UUID id
    );


    @Operation(summary = "Delete", description = "Удаление жанра")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Жанр успешно удален"
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @PathVariable
            @Parameter(name = "id", description = "Идентификатор жанра", required = true, in = ParameterIn.PATH)
            UUID id
    );

    @Operation(summary = "Get All", description = "Получение жанров с фильтрами")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Жанры успешно получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GenreListDataDto.class)
                            )
                    )
            }
    )
    @GetMapping
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    GenreListDataDto getAll(
            @RequestParam(name = "name", required = false)
            @Parameter(name = "name", description = "Название жанра", in = ParameterIn.QUERY)
            String name,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            @Parameter(name = "page", description = "Номер страницы с данными", in = ParameterIn.QUERY)
            int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "100")
            @Parameter(name = "per_page", description = "Колицество записей на одной странице", in = ParameterIn.QUERY)
            int perPage
    );
}
