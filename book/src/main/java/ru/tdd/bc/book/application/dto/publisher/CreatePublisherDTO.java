package ru.tdd.bc.book.application.dto.publisher;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 01.08.2026
 */
@Schema(description = "Dto запроса на создание издателч")
public class CreatePublisherDTO {

    @Schema(
            name = "name",
            description = "Название издателя",
            type = "string",
            example = "Питер",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Необходимо указать название издателя")
    private String name;

    @Schema(
            name = "url",
            description = "Электронный адрес издателя",
            type = "string",
            format = "url",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @URL
    private String url;

    @Schema(
            name = "country_id",
            description = "Идентификатор страны издателя",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID countryId;

    public CreatePublisherDTO() {}

    public CreatePublisherDTO(String name, String url, UUID countryId) {
        this.name = name;
        this.url = url;
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }
}
