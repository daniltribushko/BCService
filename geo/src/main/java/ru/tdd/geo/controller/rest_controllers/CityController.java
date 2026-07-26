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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.openapi.annotations.geo.CountryIdPathParameter;
import ru.tdd.bc.openapi.annotations.parameters.PageQueryParameter;
import ru.tdd.bc.openapi.annotations.parameters.PerPageQueryParameter;
import ru.tdd.geo.application.models.dto.geo.city.*;
import ru.tdd.geo.application.services.CityService;
import ru.tdd.geo.controller.config.OpenApiConfig;
import ru.tdd.geo.controller.open_api.annotations.CityIdPathParameter;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 17.01.2026
 * Контроллер для работы с городами
 */
@RequestMapping("/geo/cities")
@Tag(name = OpenApiConfig.CITY_CONTROLLER)
@SecurityRequirement(name = "jwtAuth")
public interface CityController {

    @Operation(summary = "Create", description = "Создание города, доступно для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Город успешно создан",
                            content = @Content(
                                    mediaType = "appliction/json",
                                    schema = @Schema(implementation = CityDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Регион или страна не найдены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Город уже создан",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные не валидны",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @PostMapping
    @Secured(value = "ROLE_ADMIN")
    ResponseEntity<CityDTO> create(
            @Valid
            @RequestBody
            CreateCityDTO dto
    );

    @Operation(summary = "Update", description = "Обновление города, доступно для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Город успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CityDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Город, регион, страна не найдены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Город уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @PutMapping("/{cityId}")
    @Secured(value = "ROLE_ADMIN")
    ResponseEntity<CityDTO> update(
            @NotNull
            @PathVariable("cityId")
            @CityIdPathParameter
            UUID cityId,
            @Valid
            @RequestBody
            UpdateCityDTO dto
    );

    @Operation(summary = "Find By Id", description = "Получение города по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Город удачно получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CityDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Город не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @GetMapping("/{cityId}")
    @Secured("ROLE_USER")
    ResponseEntity<CityDetailsDTO> findById(
            @NotNull
            @PathVariable("cityId")
            @CityIdPathParameter
            UUID cityId
    );

    @Operation(summary = "Delete", description = "Удаление города")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "Город удачно удален"
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Город не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{cityId}")
    @Secured(value = "ROLE_ADMIN")
    ResponseEntity<?> delete(
            @NotNull
            @PathVariable("cityId")
            @CityIdPathParameter
            UUID cityId
    );

    @Operation(summary = "Find All", description = "Получение городов с фильтрами")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Города успешно получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CityListData.class)
                            )
                    )
            }
    )
    @GetMapping
    @Secured(value = "ROLE_USER")
    ResponseEntity<CityListData> findAll(
            @RequestParam(name = "name", required = false)
            @Parameter(name = "name", description = "Название города", in = ParameterIn.QUERY)
            String name,
            @RequestParam(name = "region_name", required = false)
            @Parameter(name = "region_name", description = "Название региона", in = ParameterIn.QUERY)
            String regionName,
            @RequestParam(name = "country_name", required = false)
            @Parameter(name = "country_name", description = "Название страны", in = ParameterIn.QUERY)
            String countryName,
            @PageQueryParameter
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @PerPageQueryParameter
            @RequestParam(name = "per_page", defaultValue = "100")
            int perPage
    );
}
