package ru.tdd.geo.application.models.dto.geo.region;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * DTO списка регионов
 */
@Schema(description = "Dto со списком регионов")
public class RegionListData extends ListData<RegionDTO> {
    public RegionListData() {
    }

    public RegionListData(List<RegionDTO> data, int totalPages, int totalCount, int count, int page) {
        super(data, totalPages, totalCount, count, page);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<RegionListData, RegionDTO> {

        @Override
        public RegionListData build() {
           return build(new RegionListData());
        }
    }
}
