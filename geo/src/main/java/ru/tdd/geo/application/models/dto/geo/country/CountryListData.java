package ru.tdd.geo.application.models.dto.geo.country;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO со списком стран
 */
@Schema(description = "Dto списка стран")
public class CountryListData extends ListData<CountryDTO> {

    public CountryListData() {
    }

    public CountryListData(List<CountryDTO> data, int totalPages, long totalCount, int count, int page) {
        super(data, totalPages, totalCount, count, page);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<CountryListData, CountryDTO> {

        @Override
        public CountryListData build() {
            return build(new CountryListData());
        }
    }
}
