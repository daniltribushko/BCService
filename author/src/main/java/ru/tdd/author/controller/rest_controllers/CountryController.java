package ru.tdd.author.controller.rest_controllers;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tdd.author.application.dto.countries.CountryListDTO;
import ru.tdd.author.controller.confing.OpenApiConfig;

/**
 * @author Tribushko Danil
 * @since 05.03.2026
 * Контроллер для работы со странами
 */
@SecurityRequirement(name = "jwtAuth")
@RequestMapping("/countries")
@Tag(name = OpenApiConfig.COUNTRY_CONTROLLER)
public interface CountryController {

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
