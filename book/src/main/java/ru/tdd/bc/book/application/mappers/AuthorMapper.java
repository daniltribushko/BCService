package ru.tdd.bc.book.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tdd.bc.book.application.dto.authors.AuthorDTO;
import ru.tdd.bc.book.application.dto.authors.CreateAuthorDTO;
import ru.tdd.bc.book.database.entities.Author;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * Маппер авторов
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CountryMapper.class})
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "country", ignore = true)
    Author toEntity(CreateAuthorDTO dto);

    AuthorDTO toDto(Author author);

    AuthorDTO toDetailsDto(Author author);

}

