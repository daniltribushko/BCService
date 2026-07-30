package ru.tdd.bc.book.application.dto.countries;

import ru.tdd.bc.dto.ListData;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
public class CountryListDTO extends ListData<CountryDTO> {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<CountryListDTO, CountryDTO> {
        @Override
        public CountryListDTO build() {
            return build(new CountryListDTO());
        }
    }
}
