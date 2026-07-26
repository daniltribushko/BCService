package ru.tdd.geo.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.application.services.CountryService;
import ru.tdd.geo.controller.rest_controllers.CountryController;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 14.06.2026
 */
@RestController
public class CountryControllerImp implements CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryControllerImp(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public ResponseEntity<CountryDTO> create(CreateCountryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(dto));
    }

    @Override
    public ResponseEntity<CountryDTO> update(UUID countryId, UpdateCountryDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.update(countryId, dto));
    }

    @Override
    public ResponseEntity<?> delete(UUID countryId) {
        countryService.delete(countryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<CountryDetailsDTO> findById(UUID countryId) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getById(countryId));
    }

    @Override
    public ResponseEntity<CountryListData> findAll(String name, int page, int perPage) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getAll(name, page, perPage));
    }
}
