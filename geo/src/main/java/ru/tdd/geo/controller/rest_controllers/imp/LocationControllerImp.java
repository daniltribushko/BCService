package ru.tdd.geo.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationListData;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.services.LocationService;
import ru.tdd.geo.controller.rest_controllers.LocationController;

import java.util.UUID;

@RestController
public class LocationControllerImp implements LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationControllerImp(
            LocationService locationService
    ) {
        this.locationService = locationService;
    }

    @Override
    public ResponseEntity<LocationDTO> create(CreateLocationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(dto));
    }

    @Override
    public ResponseEntity<LocationDTO> update(UUID locationId, UpdateLocationDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.update(locationId, dto));
    }

    @Override
    public ResponseEntity<?> delete(UUID locationId) {
        locationService.delete(locationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<LocationDTO> findById(UUID locationId) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.getById(locationId));
    }

    @Override
    public ResponseEntity<LocationListData> findAll(
            String name,
            String cityName,
            String regionName,
            String countryName,
            int page,
            int perPage
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.getAll(name, cityName, regionName, countryName, page, perPage));
    }
}
