package ru.tdd.user.application.models.dto;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 11.02.2026
 * DTO списка пользователей
 */
public class UserListDTO {

    private List<UserDetailsDTO> data;

    public UserListDTO() {}

    public UserListDTO(List<UserDetailsDTO> data) {
        this.data = data;
    }

    public List<UserDetailsDTO> getData() {
        return data;
    }

    public void setData(List<UserDetailsDTO> data) {
        this.data = data;
    }
}
