package ru.tdd.geo.database.entities;

import jakarta.persistence.*;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Таблица небольшой локации
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "city"}))
public class Location extends BaseEntity implements BaseNameEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "city", nullable = false)
    private City city;

    public Location() {}

    public Location(String name, City city) {
        this.name = name;
        this.city = city;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
