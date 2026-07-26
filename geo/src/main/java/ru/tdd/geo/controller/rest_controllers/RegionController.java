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
import ru.tdd.geo.application.models.dto.geo.region.*;
import ru.tdd.geo.controller.config.OpenApiConfig;
import ru.tdd.geo.controller.open_api.annotations.RegionIdPathParameter;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 10.01.2026
 * Контроллер для регионов
 */
@RequestMapping("/geo/regions")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.REGION_CONTROLLER)
public interface RegionController {

    @Operation(summary = "Create", description = "Создание региона, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Регион успешно создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Страна не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Регион уже создан",
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
    @Secured("ROLE_ADMIN")
    ResponseEntity<RegionDTO> create(
            @Valid
            @RequestBody
            CreateRegionDTO dto
    );

    @Operation(summary = "Update", description = "Обновление региона")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Регион обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Регион или страна не надены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Регион уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<RegionDTO> update(
            @PathVariable
            @RegionIdPathParameter
            UUID id,
            @Valid
            @RequestBody
            UpdateRegionDTO dto
    );

    @Operation(summary = "Find By Id", description = "Получение региона по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Регион успешно получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegionDetailsDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Регион не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    ResponseEntity<RegionDetailsDTO> findById(
            @NotNull
            @PathVariable
            UUID id
    );

    @Operation(summary = "Delete", description = "Удаление региона, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Регион удален"),
                    @ApiResponse(
                            responseCode = "404", description = "Регион не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<?> delete(
            @NotNull
            @PathVariable
            @RegionIdPathParameter
            UUID id
    );

    @Operation(summary = "Find All", description = "Получение списка региона с фильтрацией")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Регионы получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegionListData.class)
                            )
                    )
            }
    )
    @GetMapping
    @Secured("ROLE_USER")
    ResponseEntity<RegionListData> findAll(
            @RequestParam(name = "name", required = false)
            @Parameter(name = "name", description = "Название региона", in = ParameterIn.QUERY)
            String name,
            @RequestParam(name = "country_name", required = false)
            @Parameter(name = "country_name", description = "Название страны", in = ParameterIn.QUERY)
            String countryName,
            @PageQueryParameter
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "100")
            @PerPageQueryParameter
            int perPage
    );
}
