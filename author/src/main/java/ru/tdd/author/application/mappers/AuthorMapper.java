package ru.tdd.author.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tdd.author.application.dto.authors.AuthorDTO;
import ru.tdd.author.application.dto.authors.AuthorDetailsDTO;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.database.entitites.Author;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * Маппер авторов
 */
@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", source = "author.id")
    @Mapping(target = "country", source = "countryDto")
    AuthorDTO toDto(Author author, CountryDTO countryDto);

    @Mapping(target = "id", source = "author.id")
    @Mapping(target = "country", source = "countryDto")
    AuthorDetailsDTO toDetailsDto(Author author, CountryDTO countryDto);
}
