package ru.tdd.geo.application.models.dto.geo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO списка локаций
 */
@Schema(description = "Dto списка локаций")
public class LocationListData extends ListData<LocationDTO> {

    public LocationListData() {
    }

    public LocationListData(List<LocationDTO> data, int totalPages, long totalCount, int count, int page) {
        super(data, totalPages, totalCount, count, page);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<LocationListData, LocationDTO> {

        @Override
        public LocationListData build() {
            return build(new LocationListData());
        }
    }
}
