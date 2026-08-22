package ru.tdd.bc.book.database.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import ru.tdd.bc.database.entity.EntityVersion;
import ru.tdd.bc.database.validators.annotations.DefaultSize;
import ru.tdd.bc.database.validators.annotations.NotBlankSize;

import java.time.LocalDate;

/**
 * @author Tribushko Danil
 * @since 18.02.2026
 * Сущность автора книги
 */
@Entity
@Table(name = "author", uniqueConstraints = @UniqueConstraint(columnNames = {"last_name", "middle_name", "first_name", "birthday"}))
public class Author extends EntityVersion {

    @NotBlankSize
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @DefaultSize
    @Column(name = "middle_name")
    private String middleName;

    @NotBlankSize
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlankSize
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Past(message = "Неверная дата рождения")
    @Column(name = "birthday")
    private LocalDate birthday;

    public Author() {}

    public Author(String lastName, String middleName, String firstName, Country country, LocalDate birthday) {
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.country = country;
        this.birthday = birthday;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}

