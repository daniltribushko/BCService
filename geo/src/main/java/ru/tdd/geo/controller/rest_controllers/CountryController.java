package ru.tdd.geo.controller.rest_controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.openapi.annotations.geo.CountryIdPathParameter;
import ru.tdd.bc.openapi.annotations.parameters.PageQueryParameter;
import ru.tdd.bc.openapi.annotations.parameters.PerPageQueryParameter;
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.controller.config.OpenApiConfig;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 06.01.2026
 * Контроллер для работы со странами
 */
@RequestMapping("/geo/countries")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.COUNTRY_CONTROLLER)
public interface CountryController {

    @Operation(summary = "Create", description = "Создание страны, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "Страна успешно создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CountryDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Страна уже создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
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
    @Secured(value = "ROLE_ADMIN")
    ResponseEntity<CountryDTO> create(
            @Valid
            @RequestBody
            CreateCountryDTO dto
    );

    @Operation(summary = "Update", description = "Обновление страны, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Страна успешно обновлена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CountryDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Страна с указанным идентификатором не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Страна уже создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @PutMapping("/{countryId}")
    @Secured(value = "ROLE_ADMIN")
    ResponseEntity<CountryDTO> update(
            @NotNull
            @PathVariable
            @CountryIdPathParameter
            UUID countryId,
            @Valid
            @RequestBody
            UpdateCountryDTO dto
    );

    @Operation(summary = "Delete", description = "Удаление страны, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "Страна успешно удалена, доступно только для администратора"
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Страна не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ExceptionDto.class
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{countryId}")
    @Secured(value = "ROLE_ADMIN")
    ResponseEntity<?> delete(
            @NotNull
            @PathVariable
            @CountryIdPathParameter
            UUID countryId
    );

    @Operation(summary = "Find By Id", description = "Поиск страны по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Страна успешно найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CountryDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Страна с указанным идентификатором не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @GetMapping("/{countryId}")
    @Secured(value = "ROLE_USER")
    ResponseEntity<CountryDetailsDTO> findById(
            @NotNull
            @PathVariable
            @CountryIdPathParameter
            UUID countryId
    );

    @Operation(summary = "Find All", description = "Получить список странн с определенными фильтрами")
    @ApiResponses(
            value = @ApiResponse(
                    responseCode = "200", description = "Страны получены",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CountryListData.class)
                    )
            )
    )
    @GetMapping
    @Secured(value = "ROLE_USER")
    ResponseEntity<CountryListData> findAll(
            @RequestParam(name = "name", required = false)
            @Parameter(name = "name", description = "Название страны", in = ParameterIn.QUERY)
            String name,
            @PageQueryParameter
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @PerPageQueryParameter
            @RequestParam(name = "per_page", required = false, defaultValue = "10")
            int perPage
    );
}
