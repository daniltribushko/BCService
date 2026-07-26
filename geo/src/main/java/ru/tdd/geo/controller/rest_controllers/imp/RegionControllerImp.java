package ru.tdd.geo.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.geo.application.models.dto.geo.region.*;
import ru.tdd.geo.application.services.RegionService;
import ru.tdd.geo.controller.rest_controllers.RegionController;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 25.06.2026
 */
@RestController
public class RegionControllerImp implements RegionController {

    private final RegionService regionService;

    @Autowired
    public RegionControllerImp(RegionService regionService) {
        this.regionService = regionService;
    }

    @Override
    public ResponseEntity<RegionDTO> create(CreateRegionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionService.create(dto));
    }

    @Override
    public ResponseEntity<RegionDTO> update(UUID id, UpdateRegionDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.update(id, dto));
    }

    @Override
    public ResponseEntity<RegionDetailsDTO> findById(UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getById(id));
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        regionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<RegionListData> findAll(String name, String countryName, int page, int perPage) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getAll(name, countryName, page, perPage));
    }
}
