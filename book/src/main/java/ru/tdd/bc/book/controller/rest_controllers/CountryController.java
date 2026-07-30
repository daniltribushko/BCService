package ru.tdd.bc.book.controller.rest_controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.countries.CountryListDTO;
import ru.tdd.bc.book.controller.configs.OpenApiConfig;
import ru.tdd.bc.openapi.annotations.geo.CountryIdPathParameter;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.03.2026
 * Контроллер для работы со странами
 */
@SecurityRequirement(name = "jwtAuth")
@RequestMapping("/book/countries")
@Tag(name = OpenApiConfig.COUNTRY_CONTROLLER)
public interface CountryController {

    @Operation(summary = "Get By Id", description = "Получить страну по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Страна получена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CountryDTO.class)
                            )
                    )
            }
    )
    @Secured("ROLE_USER")
    @GetMapping("/{countryId}")
    ResponseEntity<CountryDTO> getById(
            @PathVariable
            @CountryIdPathParameter
            UUID countryId
    );

    @Operation(summary = "Get All", description = "Получить список стран")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Страны получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CountryListDTO.class)
                            )
                    )
            }
    )
    @GetMapping
    @Secured("ROLE_USER")
    ResponseEntity<CountryListDTO> getAll(
            @Parameter(name = "name", description = "Название страны", in = ParameterIn.QUERY)
            @RequestParam(name = "name", required = false)
            String name,
            @Parameter(name = "page", description = "Номер страницы с данными", in = ParameterIn.QUERY)
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @Parameter(name = "per_page", description = "Количество стран на одной странице", in = ParameterIn.QUERY)
            @RequestParam(name = "per_page", required = false, defaultValue = "10")
            int perPage
    );
}
