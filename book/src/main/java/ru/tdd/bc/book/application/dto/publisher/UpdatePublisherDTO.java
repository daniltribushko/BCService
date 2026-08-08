package ru.tdd.bc.book.application.dto.publisher;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 01.08.2026
 */
@Schema(description = "DTO запроса на обновление издателя")
public class UpdatePublisherDTO {

    @Schema(
            name = "name",
            description = "Новое название издателя",
            type = "string",
            example = "Вече"
    )
    private String name;

    @Schema(
            name = "url",
            description = "Новый электронный адрес издателя (Если не происходит изменения адреса, надо заполнять поле, чтобы оно не удалилось)",
            type = "string",
            format = "url",
            example = "https://veche.ru"
    )
    @URL
    private String url;

    @Schema(
            name = "countryId",
            description = "Новый идентификатору страны",
            type = "string",
            format = "uuid",
            example = "9ff8ca9e-afbd-4799-872d-796886b26dca"
    )
    private UUID countryId;

    public UpdatePublisherDTO() {}

    public UpdatePublisherDTO(String name, String url, UUID countryId) {
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
