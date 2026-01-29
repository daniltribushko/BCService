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
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.application.services.CountryService;
import ru.tdd.geo.controller.config.OpenApiConfig;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 06.01.2026
 * Контроллер для работы со странами
 */
@RestController
@RequestMapping("/geo/countries")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.COUNTRY_CONTROLLER)
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(
            CountryService countryService
    ) {
        this.countryService = countryService;
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные для создание не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PostMapping
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<CountryDTO> create(
            @Valid
            @RequestBody
            CreateCountryDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(dto));
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Страна уже создана",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "Данные для создание не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<CountryDTO> update(
            @PathVariable @NotNull UUID id,
            @Valid
            @RequestBody
            UpdateCountryDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.update(id, dto));
    }

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
                                            implementation = ExceptionDTO.class
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<?> delete(@PathVariable @NotNull UUID id) {
        countryService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

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
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @Secured(value = "ROLE_USER")
    public ResponseEntity<CountryDetailsDTO> findById(@PathVariable @NotNull UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getById(id));
    }

    @Operation(summary = "Find All", description = "Получить список странн с определенными фильтрами")
    @ApiResponses(
            value = @ApiResponse(
                    responseCode = "200", description = "Страны получены",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CountriesDTO.class)
                    )
            )
    )
    @GetMapping
    @Secured(value = "ROLE_USER")
    public ResponseEntity<CountriesDTO> findAll(
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10")
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getAll(name, page, perPage));
    }
}
