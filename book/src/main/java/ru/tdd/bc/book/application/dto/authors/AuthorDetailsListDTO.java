package ru.tdd.bc.book.application.dto.authors;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 * DTO списка авторов с подробной информацией
 */
@Schema(description = "Список авторов с детальной информацией")
public class AuthorDetailsListDTO extends ListData<AuthorDetailsDTO> {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<AuthorDetailsListDTO, AuthorDetailsDTO> {

        @Override
        public AuthorDetailsListDTO build() {
            return build(new AuthorDetailsListDTO());
        }
    }
}
