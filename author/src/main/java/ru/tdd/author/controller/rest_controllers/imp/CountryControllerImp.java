package ru.tdd.author.controller.rest_controllers.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.author.application.dto.countries.CountryListDTO;
import ru.tdd.author.application.services.CountryService;
import ru.tdd.author.controller.rest_controllers.CountryController;

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
    public ResponseEntity<CountryListDTO> getAll(String name, int page, int perPage) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getAll(name, page, perPage));
    }
}
