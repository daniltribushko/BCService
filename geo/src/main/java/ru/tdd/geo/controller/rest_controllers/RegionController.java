package ru.tdd.geo.controller.rest_controllers;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.tdd.geo.application.models.dto.ExceptionDTO;
import ru.tdd.geo.application.models.dto.geo.region.*;
import ru.tdd.geo.application.services.RegionService;
import ru.tdd.geo.controller.config.OpenApiConfig;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 10.01.2026
 * Контроллер для регионов
 */
@RestController
@RequestMapping("/geo/regions")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.REGION_CONTROLLER)
public class RegionController {

    private final RegionService regionService;

    @Autowired
    public RegionController(
            RegionService regionService
    ) {
        this.regionService = regionService;
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Регион уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<RegionDTO> create(
            @Valid
            @RequestBody
            CreateRegionDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionService.create(dto));
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Регион уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<RegionDTO> update(
            @NotNull
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateRegionDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.update(id, dto));
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<RegionDetailsDTO> findById(
            @NotNull
            @PathVariable
            UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getById(id));
    }

    @Operation(summary = "Delete", description = "Удаление региона, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Регион удален"),
                    @ApiResponse(
                            responseCode = "404", description = "Регион не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> delete(
            @NotNull
                    @PathVariable
            UUID id
    ) {
        regionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Find All", description = "Получение списка региона с фильтрацией")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Регионы получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RegionsDTO.class)
                            )
                    )
            }
    )
    @GetMapping
    @Secured("ROLE_USER")
    public ResponseEntity<RegionsDTO> findAll(
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "country-name", required = false)
            String countryName,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @RequestParam(name = "per-page", required = false, defaultValue = "100")
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getAll(name, countryName, page, perPage));
    }
}
