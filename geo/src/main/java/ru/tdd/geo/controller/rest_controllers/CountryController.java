package ru.tdd.geo.controller.rest_controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.application.services.CountryService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 06.01.2026
 * Контроллер для работы со странами
 */
@RestController
@RequestMapping("/geo/countries")
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(
            CountryService countryService
    ) {
        this.countryService = countryService;
    }

    @PostMapping
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<CountryDTO> create(
            @Valid
            @RequestBody
            CreateCountryDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(dto));
    }

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

    @DeleteMapping("/{id}")
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<?> delete(@PathVariable @NotNull UUID id) {
        countryService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    @Secured(value = "ROLE_USER")
    public ResponseEntity<CountryDetailsDTO> findById(@PathVariable @NotNull UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getById(id));
    }

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
