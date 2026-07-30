package ru.tdd.bc.book.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.countries.CountryListDTO;
import ru.tdd.bc.book.application.services.CountryService;
import ru.tdd.bc.book.controller.rest_controllers.CountryController;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.03.2026
 */
@RestController
public class CountryControllerImp implements CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryControllerImp(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public ResponseEntity<CountryDTO> getById(UUID countryId) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getById(countryId));
    }

    @Override
    public ResponseEntity<CountryListDTO> getAll(String name, int page, int perPage) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getAll(name, page, perPage));
    }
}
