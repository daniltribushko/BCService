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
import ru.tdd.bc.openapi.annotations.parameters.PageQueryParameter;
import ru.tdd.bc.openapi.annotations.parameters.PerPageQueryParameter;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationListData;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.controller.config.OpenApiConfig;
import ru.tdd.geo.controller.open_api.annotations.LocationIdPathParameter;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.01.2026
 * Контроллер для работы с локациями
 */
@RequestMapping("/geo/locations")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.LOCATION_CONTROLLER)
public interface LocationController {

    @Operation(summary = "Create", description = "Создание локации, доступно только для администрации")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Локация успешно создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    contentSchema = @Schema(implementation = LocationDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Город не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    contentSchema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Локация уже создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    contentSchema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    contentSchema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @PostMapping
    ResponseEntity<LocationDTO> create(
            @Valid
            @RequestBody
            CreateLocationDTO dto
    );

    @Operation(summary = "Update", description = "Обновление локации, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Локация обновлена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LocationDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Локация или город не найдены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Локация уже создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @PutMapping("/{locationId}")
    ResponseEntity<LocationDTO> update(
            @NotNull
            @PathVariable
            @LocationIdPathParameter
            UUID locationId,
            @Valid
            @RequestBody
            UpdateLocationDTO dto
    );

    @Operation(summary = "Delete", description = "Удаление локации, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Локация удалена"),
                    @ApiResponse(
                            responseCode = "404", description = "Локация не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{locationId}")
    ResponseEntity<?> delete(
            @NotNull
            @PathVariable
            @LocationIdPathParameter
            UUID locationId
    );

    @Operation(summary = "Find By Id", description = "Получение локации по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Локация получена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LocationDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Локация не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @GetMapping("/{locationId}")
    ResponseEntity<LocationDTO> findById(
            @NotNull
            @PathVariable
            @LocationIdPathParameter
            UUID locationId
    );

    @Operation(summary = "Find All", description = "Получение списка локаций с фильтрацией")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Локации получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LocationListData.class)
                            )
                    )
            }
    )
    @GetMapping
    ResponseEntity<LocationListData> findAll(
            @RequestParam(name = "name", required = false)
            @Parameter(name = "name", description = "Название локации", in = ParameterIn.QUERY)
            String name,
            @RequestParam(name = "city_name", required = false)
            @Parameter(name = "city_name", description = "Название Города", in = ParameterIn.QUERY)
            String cityName,
            @RequestParam(name = "region_name", required = false)
            @Parameter(name = "region_name", description = "Название Региона", in = ParameterIn.QUERY)
            String regionName,
            @RequestParam(name = "country_name", required = false)
            @Parameter(name = "country_name", description = "Название Города", in = ParameterIn.QUERY)
            String countryName,
            @PageQueryParameter
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @PerPageQueryParameter
            @RequestParam(name = "per_page", defaultValue = "100")
            int perPage
    );
}
