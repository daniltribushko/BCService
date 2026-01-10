package ru.tdd.geo.application.models.dto.geo.country;

import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.database.entities.Country;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO страны с подробной информацией
 */
public class CountryDetailsDTO {

    private UUID id;

    private String name;

    private ZoneId zoneId;

    private List<RegionDTO> regions;

    public CountryDetailsDTO() {}

    public CountryDetailsDTO(UUID id, String name, ZoneId zoneId, List<RegionDTO> regions) {
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
        this.regions = regions;
    }

    public static CountryDetailsDTO mapFromEntity(Country country) {
        return new CountryDetailsDTO(
                country.getId(),
                country.getName(),
                country.getZoneId(),
                country.getRegions().stream().map(RegionDTO::mapFromEntity)
                        .sorted(Comparator.comparing(RegionDTO::getName))
                        .toList()

        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public List<RegionDTO> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionDTO> regions) {
        this.regions = regions;
    }
}
