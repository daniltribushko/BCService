package ru.tdd.geo.database.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Таблица страны
 */
@Entity
public class Country extends BaseEntity implements BaseNameEntity {

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "zone_id", nullable = false)
    private ZoneId zoneId = ZoneId.systemDefault();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "country")
    private Set<Region> regions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "country")
    private Set<City> cities = new HashSet<>();

    public Country() {}

    public Country(String name, ZoneId zoneId, Set<Region> regions, Set<City> cities) {
        this.name = name;
        this.zoneId = zoneId;
        this.regions = regions;
        this.cities = cities;
    }

    public Country(String name) {
        this.name = name;
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public void deleteCity(City city) {
        cities.remove(city);
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public Set<Region> getRegions() {
        return regions;
    }

    public void setRegions(Set<Region> regions) {
        this.regions = regions;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }
}
