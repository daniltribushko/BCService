package ru.tdd.bc.dictionaries.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 */
@Schema(description = "Dto справочника")
public class DictionaryDto {

    @Schema(
            name = "id",
            description = "Идентификатор справочника",
            type = "string",
            format = "uuid"
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Название справочника",
            type = "string",
            example = "Научная фантастика"
    )
    private String name;

    public DictionaryDto() {}

    public DictionaryDto(UUID id, String name) {
        this.id = id;
        this.name = name;
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
}
