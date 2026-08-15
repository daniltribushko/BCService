package ru.tdd.bc.book.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tdd.bc.book.database.entities.Genre;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 08.08.2026
 * Маппер жанров книг
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    @Mapping(target = "id", ignore = true)
    Genre toEntity(CreateDictionaryDto dto);

    DictionaryDto toDto(Genre entity);

    List<DictionaryDto> toDto(List<Genre> entities);
}
