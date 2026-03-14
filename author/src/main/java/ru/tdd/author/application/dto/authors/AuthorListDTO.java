package ru.tdd.author.application.dto.authors;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * DTO списка пользователей
 */
public class AuthorListDTO {

    private List<AuthorDTO> data;

    public AuthorListDTO() {}

    public AuthorListDTO(List<AuthorDTO> data) {
        this.data = data;
    }

    public List<AuthorDTO> getData() {
        return data;
    }

    public void setData(List<AuthorDTO> data) {
        this.data = data;
    }
}
