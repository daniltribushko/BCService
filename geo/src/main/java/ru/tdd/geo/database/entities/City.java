package ru.tdd.geo.database.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Таблица города
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "region", "country"}))
public class City extends BaseEntity implements BaseNameEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "region")
    private Region region;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "country", nullable = false)
    private Country country;

    public City() {
        super();
    }

    public City(String name, Region region, Country country) {
        this.name = name;
        this.region = region;
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
