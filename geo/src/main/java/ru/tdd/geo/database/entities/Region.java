package ru.tdd.geo.database.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Таблица региона
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "country"}))
public class Region extends BaseEntity implements BaseNameEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Country country;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "region")
    private Set<City> cities = new HashSet<>();

    public Region() {}

    public Region(String name, Country country, Set<City> cities) {
        this.name = name;
        this.country = country;
        this.cities = cities;
    }

    public Region(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }
}
