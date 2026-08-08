package ru.tdd.bc.book.application.dto.publisher;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 01.08.2026
 */
@Schema(description = "DTO издателя книг")
public class PublisherDTO {

    @Schema(
            name = "id",
            description = "Идентификатор издателя",
            type = "string",
            format = "uuid",
            example = "ebcdbaa1-3afc-4bb7-abb4-774437b7fc72"
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Названия издательства",
            type = "string",
            example = "Питер"
    )
    private String name;

    @Schema(
            name = "url",
            description = "Электронный адрес издательства",
            type = "string",
            format = "url",
            example = "'https://www.piter.com"
    )
    private String url;

    @Schema(
            name = "country",
            description = "Страна издателя",
            type = "object"
    )
    private CountryDTO country;

    @Schema(
            name = "creationTime",
            description = "Время создания издателя",
            type = "string",
            format = "date-time",
            example = ""
    )
    private LocalDateTime creationTime;

    @Schema(
            name = "updateTime",
            description = "Время последнего обновления издателя",
            type = "string",
            format = "date-time",
            example = ""
    )
    private LocalDateTime updateTime;

    public PublisherDTO() {}

    public PublisherDTO(UUID id, String name, String url, CountryDTO country, LocalDateTime creationTime, LocalDateTime updateTime) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.country = country;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
