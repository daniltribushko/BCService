package ru.tdd.bc.book.application.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.ISBN;
import ru.tdd.bc.book.database.validations.PastYear;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 17.08.2026
 */
@Schema(description = "ДТО запроса на создание книги")
public class CreateBookDTO {

    @Schema(
            name = "title",
            description = "Заголовок книги",
            type = "string",
            maxLength = 255,
            example = "Капитанская дочь",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Size(max = 255)
    @NotBlank
    private String title;

    @Schema(
            name = "isbn",
            description = "ISBN номер книги",
            type = "string",
            example = "978-5-00242-168-8",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    @ISBN(type = ISBN.Type.ANY)
    private String isbn;

    @Schema(
            name = "publisherId",
            description = "Идентификатор издателя",
            type = "string",
            format = "uuid",
            example = "cc120565-fe26-47ef-830d-646a5c5b4686",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID publisherId;


    @Schema(
            name = "publishingYear"
    )
    @PastYear
    private Integer publishingYear;
}
