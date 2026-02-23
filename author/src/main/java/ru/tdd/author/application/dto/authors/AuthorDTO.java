package ru.tdd.author.application.dto.authors;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.author.application.dto.countries.CountryDTO;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * DTO автора книги
 */
public class AuthorDTO {

    @Schema(
            name = "id",
            description = "Идентификатор автора",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;

    @Schema(
            name = "lastName",
            description = "Фамилия автора",
            type = "string",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String lastName;

    @Schema(
            name = "middleName",
            description = "Отчество автора",
            type = "string",
            example = "Иванович",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String middleName;

    @Schema(
            name = "firstName",
            description = "Имя автора",
            type = "string",
            example = "Иван",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @Schema(
            name = "country",
            description = "Страна автора",
            type = "object",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CountryDTO country;

    public AuthorDTO() {}

    public AuthorDTO(UUID id, String lastName, String middleName, String firstName, CountryDTO country) {
        this.id = id;
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.country = country;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }
}
