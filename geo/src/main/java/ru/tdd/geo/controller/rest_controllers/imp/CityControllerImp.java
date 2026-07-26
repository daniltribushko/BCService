package ru.tdd.geo.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.geo.application.models.dto.geo.city.*;
import ru.tdd.geo.application.services.CityService;
import ru.tdd.geo.controller.rest_controllers.CityController;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 12.06.2026
 */
@RestController
public class CityControllerImp implements CityController {

    private final CityService cityService;

    @Autowired
    public CityControllerImp(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public ResponseEntity<CityDTO> create(CreateCityDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(dto));
    }

    @Override
    public ResponseEntity<CityDTO> update(UUID id, UpdateCityDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(cityService.update(id, dto));
    }

    @Override
    public ResponseEntity<CityDetailsDTO> findById(UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(cityService.getById(id));
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        cityService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<CityListData> findAll(String name, String regionName, String countryName, int page, int perPage) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cityService.getAll(name, regionName, countryName, page, perPage));
    }
}
