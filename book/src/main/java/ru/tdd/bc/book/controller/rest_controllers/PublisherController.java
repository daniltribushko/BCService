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
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherListDTO;
import ru.tdd.bc.book.application.dto.publisher.UpdatePublisherDTO;
import ru.tdd.bc.book.controller.configs.OpenApiConfig;
import ru.tdd.bc.book.controller.open_api.annotations.CountryNameQueryParameter;
import ru.tdd.bc.dto.ExceptionDto;
import ru.tdd.bc.openapi.annotations.book.PublisherIdPathParameter;
import ru.tdd.bc.openapi.annotations.parameters.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.08.2026
 * Контроллер для работы с издателями
 */
@RequestMapping("/books/publishers")
@SecurityRequirement(name = "jwtAuth")
@Tag(name = OpenApiConfig.PUBLISHER_CONTROLLER)
public interface PublisherController {

    @Operation(summary = "Create", description = "Создание издателей, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Издатель создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PublisherDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Издатель уже создан",
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
    ResponseEntity<PublisherDTO> create(@Valid @RequestBody CreatePublisherDTO dto);

    @Operation(summary = "Update", description = "Обновление издателя, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Издатель успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PublisherDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Издатель не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Издатель уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @PutMapping("/{publisherId}")
    ResponseEntity<PublisherDTO> update(
            @PathVariable
            @PublisherIdPathParameter
            UUID publisherId,
            @RequestBody UpdatePublisherDTO dto
    );

    @Operation(summary = "Get By Id", description = "Получение издателя по идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Издатель успешно получен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PublisherDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Издатель не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_USER")
    @GetMapping("/{publisherId}")
    ResponseEntity<PublisherDTO> getById(
            @PathVariable
            @PublisherIdPathParameter
            UUID publisherId
    );

    @Operation(summary = "Delete", description = "Удаление издателя, доступно только для администратора")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Издатель успешно удалён"),
                    @ApiResponse(
                            responseCode = "404", description = "Издатель не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDto.class)
                            )
                    )
            }
    )
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{publisherId}")
    ResponseEntity<?> delete(
            @PathVariable
            @PublisherIdPathParameter
            UUID publisherId
    );

    @Operation(summary = "Get All", description = "Получение списка издателей")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Издатели успешно получены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PublisherListDTO.class)
                            )
                    )
            }
    )
    @GetMapping
    @Secured("ROLE_USER")
    ResponseEntity<PublisherListDTO> getAll(
            @RequestParam(name = "name", required = false)
            @Parameter(name = "name", description = "Название издателя", in = ParameterIn.QUERY)
            String name,
            @RequestParam(name = "country_name", required = false)
            @CountryNameQueryParameter
            String countryName,
            @StartCreationTimeQueryParameter
            @RequestParam(name = "start_creation_time", required = false)
            LocalDateTime startCreationTime,
            @EndCreationTimeQueryParameter
            @RequestParam(name = "end_creation_time", required = false)
            LocalDateTime endCreationTime,
            @StartUpdateTimeQueryParameter
            @RequestParam(name = "start_update_time", required = false)
            LocalDateTime startUpdateTime,
            @EndUpdateTimeQueryParameter
            @RequestParam(name = "end_update_time", required = false)
            LocalDateTime endUpdateTime,
            @PageQueryParameter
            @RequestParam(name = "page", required = false, defaultValue = "0")
            int page,
            @PerPageQueryParameter
            @RequestParam(name = "per_page", required = false, defaultValue = "10")
            int perPage
    );
}
