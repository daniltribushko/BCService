package ru.tdd.author.application.dto.authors;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * DTO списка авторов с подробной информацией
 */
public class AuthorDetailsListDTO {

    private List<AuthorDetailsDTO> data;

    public AuthorDetailsListDTO() {}

    public AuthorDetailsListDTO(List<AuthorDetailsDTO> data) {
        this.data = data;
    }

    public List<AuthorDetailsDTO> getData() {
        return data;
    }

    public void setData(List<AuthorDetailsDTO> data) {
        this.data = data;
    }
}
