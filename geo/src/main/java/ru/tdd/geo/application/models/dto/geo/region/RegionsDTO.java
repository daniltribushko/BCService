package ru.tdd.geo.application.models.dto.geo.region;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * DTO списка регионов
 */
public class RegionsDTO {

    private List<RegionDTO> data;

    public RegionsDTO() {}

    public RegionsDTO(List<RegionDTO> data) {
        this.data = data;
    }

    public List<RegionDTO> getData() {
        return data;
    }

    public void setData(List<RegionDTO> data) {
        this.data = data;
    }
}
