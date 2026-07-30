package ru.tdd.bc.book.application.dto.authors;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * DTO списка пользователей
 */
@Schema(description = "Список авторов")
public class AuthorListDTO extends ListData<AuthorDTO> {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<AuthorListDTO, AuthorDTO> {
        @Override
        public AuthorListDTO build() {
            return super.build(new AuthorListDTO());
        }
    }
}
