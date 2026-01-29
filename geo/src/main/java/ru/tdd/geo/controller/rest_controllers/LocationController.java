package ru.tdd.geo.controller.rest_controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.geo.application.models.dto.ExceptionDTO;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationsDTO;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.services.LocationService;
import ru.tdd.geo.controller.config.OpenApiConfig;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.01.2026
 * Контроллер для работы с локациями
 */
@RestController
@RequestMapping("/geo/locations")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.LOCATION_CONTROLLER)
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(
            LocationService locationService
    ) {
        this.locationService = locationService;
    }

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
                                    contentSchema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Локация уже создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    contentSchema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    contentSchema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<LocationDTO> create(
            @Valid
            @RequestBody
            CreateLocationDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(dto));
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Локация уже создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> update(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateLocationDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.update(id, dto));
    }

    @Operation(summary = "Delete", description = "Удаление локации, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Локация удалена"),
                    @ApiResponse(
                            responseCode = "404", description = "Локация не найдена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable
            UUID id
    ) {
        locationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.getById(id));
    }

    @Operation(summary = "Find All", description = "Получение списка локаций с фильтрацией")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Локации получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LocationsDTO.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<LocationsDTO> findAll(
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "city-name", required = false)
            String cityName,
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @RequestParam(name = "per-page", defaultValue = "100")
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.getAll(name, cityName, page, perPage));
    }
}
