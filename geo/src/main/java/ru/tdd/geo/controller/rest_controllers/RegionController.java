package ru.tdd.geo.controller.rest_controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.tdd.geo.application.models.dto.geo.region.*;
import ru.tdd.geo.application.services.RegionService;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 10.01.2026
 * Контроллер для регионов
 */
@RestController
@RequestMapping("/geo/regions")
public class RegionController {

    private final RegionService regionService;

    @Autowired
    public RegionController(
            RegionService regionService
    ) {
        this.regionService = regionService;
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<RegionDTO> create(
            @Valid
            @RequestBody
            CreateRegionDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionService.create(dto));
    }

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

    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<RegionDetailsDTO> findByIf(
            @NotNull
            @PathVariable
            UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getById(id));
    }

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

    @GetMapping("/all")
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
