package ru.tdd.bc.book.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.tdd.bc.book.application.dto.authors.AuthorDTO;
import ru.tdd.bc.book.application.dto.authors.AuthorDetailsDTO;
import ru.tdd.bc.book.database.entities.Author;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * Маппер авторов
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CountryMapper.class})
public interface AuthorMapper {

    AuthorDTO toDto(Author author);

    AuthorDetailsDTO toDetailsDto(Author author);

}

