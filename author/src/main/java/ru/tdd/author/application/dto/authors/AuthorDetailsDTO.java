package ru.tdd.author.application.dto.authors;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.author.application.dto.countries.CountryDTO;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * Детальная информация автоа
 */
public class AuthorDetailsDTO {

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
            description = "Отчество автоа",
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

    @Schema(
            name = "creationTime",
            description = "Время создания автора",
            type = "string",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime creationTime;

    @Schema(
            name = "updateTime",
            description = "Время обновления последнего автора",
            type = "string",
            format = "date-time",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDateTime updateTime;

    public AuthorDetailsDTO(){}

    public AuthorDetailsDTO(
            UUID id,
            String lastName,
            String middleName,
            String firstName,
            CountryDTO country,
            LocalDateTime creationTime,
            LocalDateTime updateTime
    ) {
        this.id = id;
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
