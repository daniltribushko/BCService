package ru.tdd.book.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Dto запроса на получения списка жанров
 */
@Schema(description = "Dto, содержащее список жанров")
public class GenreListDataDto {

    private List<GenreDto> data;

    public GenreListDataDto() {

    }

    public GenreListDataDto(List<GenreDto> data) {
        this.data = data;
    }

    public List<GenreDto> getData() {
        return data;
    }

    public void setData(List<GenreDto> data) {
        this.data = data;
    }
}
