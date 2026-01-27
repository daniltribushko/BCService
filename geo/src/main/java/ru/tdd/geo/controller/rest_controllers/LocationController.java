package ru.tdd.geo.controller.rest_controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationsDTO;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.services.LocationService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.01.2026
 * Контроллер для работы с локациями
 */
@RestController
@RequestMapping("/geo/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(
            LocationService locationService
    ) {
        this.locationService = locationService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<LocationDTO> create(
            @Valid
            @RequestBody
            CreateLocationDTO dto
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(dto));
    }

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

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable
            UUID id
    ) {
        locationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.getById(id));
    }

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
