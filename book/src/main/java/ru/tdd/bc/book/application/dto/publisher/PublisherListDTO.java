package ru.tdd.bc.book.application.dto.publisher;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

/**
 * @author Tribushko Danil
 * @since 01.08.2026
 */
@Schema(description = "DTO списка авторов")
public class PublisherListDTO extends ListData<PublisherDTO> {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ListData.Builder<PublisherListDTO, PublisherDTO> {
        @Override
        public PublisherListDTO build() {
            return build(new PublisherListDTO());
        }
    }
}
