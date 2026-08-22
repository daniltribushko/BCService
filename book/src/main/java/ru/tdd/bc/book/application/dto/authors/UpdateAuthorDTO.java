package ru.tdd.bc.book.application.dto.authors;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * DTO запроса на обновление автора
 */
public class UpdateAuthorDTO {

    @Schema(
            name = "lastName",
            description = "Фамили автора",
            type = "string",
            example = "Петров"
    )
    private String lastName;

    @Schema(
            name = "middleName",
            description = "Отчество автора",
            type = "string",
            example = "Петрович"
    )
    private String middleName;

    @Schema(
            name = "firstName",
            description = "Имя автора",
            type = "string",
            example = "Петр"
    )
    private String firstName;

    @Schema(
            name = "countryId",
            description = "Идентификатор старны автора",
            type = "string",
            format = "uuid"
    )
    private UUID countryId;

    @Schema(
            name = "birthday",
            description = "Дата рождения",
            type = "string",
            format = "date"
    )
    private LocalDate birthday;

    public UpdateAuthorDTO() {}

    public UpdateAuthorDTO(String lastName, String middleName, String firstName, UUID countryId, LocalDate birthday) {
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.countryId = countryId;
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

    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}

