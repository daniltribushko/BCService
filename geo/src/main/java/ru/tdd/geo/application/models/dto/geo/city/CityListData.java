package ru.tdd.geo.application.models.dto.geo.city;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO списка городов
 */
@Schema(description = "Dto списка городов")
public class CityListData extends ListData<CityDTO> {

    public CityListData() {
    }

    public CityListData(List<CityDTO> data, int totalPages, long totalCount, int count, int page) {
        super(data, totalPages, totalCount, count, page);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<CityListData, CityDTO> {

        @Override
        public CityListData build() {
            return super.build(new CityListData());
        }
    }
}
