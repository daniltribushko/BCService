package ru.tdd.bc.dictionaries.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 */
@Schema(description = "Dto запроса на обновление справочника")
public class UpdateDictionaryDto {

    private String name;

    public UpdateDictionaryDto() {}

    public UpdateDictionaryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
