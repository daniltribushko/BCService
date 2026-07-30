package ru.tdd.bc.book.database.entities;

import jakarta.persistence.*;
import ru.tdd.bc.database.entity.EntityVersion;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Сущность автора книги
 */
@Entity
@Table(name = "author")
public class Author extends EntityVersion {

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public Author() {}

    public Author(String lastName, String middleName, String firstName, Country country) {
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.country = country;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}

