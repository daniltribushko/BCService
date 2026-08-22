package ru.tdd.bc.book.application.dto.authors;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * DTO запроса на создание автора
 */
public class CreateAuthorDTO {

    @Schema(
            name = "lastName",
            description = "Фамилия автора",
            type = "string",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Необходимо указать фамилию автора")
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
    @NotBlank(message = "Необходимо указать имя автора")
    private String firstName;

    @Schema(
            name = "countryId",
            description = "Идентификатор страны",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Необходимо указать идентификатор старны автора")
    private UUID countryId;

    @Schema(
            name = "birthday",
            description = "Дата рождения пользователя",
            type = "string",
            format = "date"
    )
    @Past(message = "Дата рождения автора должна быть в прошедшем времени")
    private LocalDate birthday;

    public CreateAuthorDTO() {}

    public CreateAuthorDTO(String lastName, String middleName, String firstName, UUID countryId, LocalDate birthday) {
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
