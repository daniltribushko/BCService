package ru.tdd.geo.controller.rest_controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.geo.application.models.dto.geo.city.*;
import ru.tdd.geo.application.services.CityService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 17.01.2026
 * Контроллер для работы с городами
 */
@RestController
@RequestMapping("/geo/cities")
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(
            CityService cityService
    ) {
        this.cityService = cityService;
    }

    @PostMapping
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<CityDTO> create(
            @Valid
            @RequestBody
            CreateCityDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(dto));
    }

    @PutMapping("/{id}")
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<CityDTO> update(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateCityDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(cityService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDetailsDTO> findById(
            @PathVariable
            UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(cityService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Secured(value = "ROLE_ADMIN")
    public ResponseEntity<?> delete(
            @PathVariable
            UUID id
    ) {
        cityService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<CitiesDTO> findAll(
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "region-name", required = false)
            String regionName,
            @RequestParam(name = "country-name", required = false)
            String countryName,
            @RequestParam(name = "page", defaultValue = "0")
            int page,
            @RequestParam(name = "per-page", defaultValue = "100")
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(cityService.getAll(name, regionName, countryName, page, perPage));
    }
}
